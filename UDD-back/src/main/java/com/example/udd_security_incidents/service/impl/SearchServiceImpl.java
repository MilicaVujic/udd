package com.example.udd_security_incidents.service.impl;

import ai.djl.translate.TranslateException;
import co.elastic.clients.elasticsearch._types.KnnQuery;

import org.springframework.data.elasticsearch.client.elc.NativeQuery;


import com.example.udd_security_incidents.dto.SearchDto;
import com.example.udd_security_incidents.indexmodel.IncidentsIndex;
import com.example.udd_security_incidents.indexrepository.IncidentsIndexRepository;
import com.example.udd_security_incidents.model.SearchType;
import com.example.udd_security_incidents.service.interfaces.SearchService;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import com.example.udd_security_incidents.util.VectorizationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final IncidentsIndexRepository incidentIndexRepository;

    private final ElasticsearchOperations elasticsearchTemplate;

public Page<IncidentsIndex> searchByVector(float[] queryVector, Pageable pageable) {
    if (queryVector == null || queryVector.length == 0) {
        throw new IllegalArgumentException("Query vector cannot be null or empty.");
    }

    Float[] floatObjects = new Float[queryVector.length];
    for (int i = 0; i < queryVector.length; i++) {
        floatObjects[i] = queryVector[i];
    }
    List<Float> floatList = Arrays.stream(floatObjects).collect(Collectors.toList());

    var knnQuery = new KnnQuery.Builder()
            .field("vectorizedContent")
            .queryVector(floatList)
            .numCandidates(100)
            .k(10)
            .boost(100.0f)
            .build();


    var query = NativeQuery.builder()
            .withKnnQuery(knnQuery)
            .withMaxResults(1)
            .withSearchType(null)
            .withHighlightQuery(highlight())
            .build();

    try {
        SearchHits<IncidentsIndex> searchHits = elasticsearchTemplate.search(query, IncidentsIndex.class);

        List<IncidentsIndex> summarizedResults = searchHits.stream()
                .map(hit -> {
                    IncidentsIndex result = hit.getContent();
                    result = applyHighlights(hit, result);
                    return result;
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(summarizedResults, pageable, searchHits.getTotalHits());
    } catch (Exception e) {
        log.error("Greška pri pretrazi u Elasticsearch-u", e);
    }

    return null;
}

    private HighlightQuery highlight() {
        List<HighlightField> lista=new ArrayList<HighlightField>();
        lista.add( new HighlightField("affected_organization"));
        lista.add( new HighlightField("security_organization"));

        return new HighlightQuery(new Highlight(lista), null);
    }

    @Override
public Page<IncidentsIndex> search(SearchDto searchDto, Pageable pageable) throws TranslateException, JsonProcessingException {
    if (searchDto.getSearchText().isEmpty()) {
        return Page.empty();
    }

    if (searchDto.getType().equals(SearchType.SIMPLE)) {
        List<String> tokens = new ArrayList<>(Arrays.asList(searchDto.getSearchText().split(",")));
        tokens.replaceAll(String::trim);

        if (tokens.stream().anyMatch(token -> token.contains("employee_name"))) {
            return incidentIndexRepository.getIncidentIndicesByEmployeeNameAndSeverity(
                    tokens.get(0).split(":")[1].trim(), tokens.get(1).split(":")[1].trim(), pageable);
        } else {
            return incidentIndexRepository.getIncidentIndicesBySecurityOrganizationAndAffectedOrganization(
                    tokens.get(0).split(":")[1].trim(), tokens.get(1).split(":")[1].trim(), pageable);
        }
    } else if (searchDto.getType().equals(SearchType.KNN)) {
        return searchByVector(VectorizationUtil.getEmbedding(searchDto.getSearchText()), pageable);
    } else if (searchDto.getType().equals(SearchType.FULL)) {
        return Page.empty();
    } else {
        List<String> tokens = parseQueryString(searchDto.getSearchText());

        Query query = buildSearchQuery(tokens);

        SearchHits<IncidentsIndex> searchHits = elasticsearchTemplate.search(query, IncidentsIndex.class);

        List<IncidentsIndex> summarizedResults = searchHits.stream()
                .map(hit -> {
                    IncidentsIndex result = hit.getContent();
                    result = applyHighlights(hit, result);
                    return result;
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(summarizedResults, pageable, searchHits.getTotalHits());
    }
}

    private IncidentsIndex applyHighlights(SearchHit<IncidentsIndex> hit, IncidentsIndex result) {
        Map<String, List<String>> highlightFields = hit.getHighlightFields();
        if (highlightFields.isEmpty()) {
            return result;
        }

        for (String field : highlightFields.keySet()) {
                List<String> highlightedValue = highlightFields.get(field);
                String cleanedHighlight = highlightedValue.get(0).replaceAll("<em>", "").replaceAll("</em>", "");
                String highlighted = cleanedHighlight.length()>2 ? cleanedHighlight.substring(0,2):cleanedHighlight;
                result = setFieldValue(result, field, highlighted);
            }
        return result;
    }



    private IncidentsIndex setFieldValue(IncidentsIndex result, String fieldName, String value) {
        try {
            Field field = result.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(result, value); // Postavi vrednost na odgovarajuće polje
        } catch (NoSuchFieldException e) {
            log.warn("Polje '{}' nije pronađeno u klasi '{}'", fieldName, result.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            log.error("Greška prilikom postavljanja vrednosti za polje '{}': {}", fieldName, e.getMessage());
        }
        return result;
    }





    private List<String> infixToPostfix(List<String> infixTokens, Map<String, Integer> operatorPriority) {
        Stack<String> stack = new Stack<>();
        List<String> postfix = new ArrayList<>();

        for (String token : infixTokens) {
            if (operatorPriority.containsKey(token)) {
                // Handle operators based on precedence
                while (!stack.isEmpty() && operatorPriority.getOrDefault(stack.peek(), 0) >= operatorPriority.get(token)) {
                    postfix.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equals("(")) {
                stack.push(token);
            } else if (token.equals(")")) {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                stack.pop();
            } else {
                postfix.add(token);
            }
        }

        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }

        return postfix;
    }

    private Query buildSearchQuery(List<String> tokens) {
        Map<String, Integer> operatorPriority = Map.of("NOT", 3, "AND", 2, "OR", 1);

        List<String> postfixTokens = infixToPostfix(tokens, operatorPriority);

        return NativeQuery.builder()
                .withQuery(evaluatePostfix(postfixTokens))
                .withMaxResults(5)
                .withSearchType(null)
                .withHighlightQuery(highlight())
                .build();

    }
    private List<String> parseQueryString(String queryString) {
        String[] parts = queryString.split(",");
        List<String> tokens = new ArrayList<>();
        Collections.addAll(tokens, parts);
        return tokens;
    }

    private Query evaluatePostfix(List<String> postfixTokens) {
        Stack<Criteria> stack = new Stack<>();

        for (String token : postfixTokens) {
            if (token.equals("NOT")) {
                // Unary operator
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Invalid query: NOT operator requires one operand.");
                }
                Criteria operand = stack.pop();
                stack.push(operand.not());
            } else if (token.equals("AND")) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid query: AND operator requires two operands.");
                }
                Criteria right = stack.pop();
                Criteria left = stack.pop();
                stack.push(left.and(right));
            } else if (token.equals("OR")) {
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid query: OR operator requires two operands.");
                }
                Criteria right = stack.pop();
                Criteria left = stack.pop();
                stack.push(left.or(right));
            } else {
                String[] parts = token.split(":");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid operand: " + token);
                }
                String field = parts[0];
                String value = parts[1];

                String phraseQuery = "\"" + value + "\"~" + 2; // Dodavanje slop parametra
                stack.push(Criteria.where(field).matches(phraseQuery));            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid query: unmatched operators or operands.");
        }

        return new CriteriaQuery(stack.pop());
    }


}
