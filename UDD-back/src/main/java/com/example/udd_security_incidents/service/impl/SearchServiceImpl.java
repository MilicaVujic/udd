package com.example.udd_security_incidents.service.impl;

import ai.djl.translate.TranslateException;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;

import org.elasticsearch.index.query.QueryBuilder;

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

import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.nd4j.shade.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchServiceImpl implements SearchService {
    private final IncidentsIndexRepository incidentIndexRepository;

    private final ElasticsearchOperations elasticsearchTemplate;

/*
    @Override
    public Page<DummyIndex> simpleSearch(List<String> keywords, Pageable pageable, boolean isKNN) {
        if (isKNN) {
            try {
                return searchByVector(VectorizationUtil.getEmbedding(Strings.join(keywords, " ")));
            } catch (TranslateException e) {
                log.error("Vectorization failed");
                return Page.empty();
            }
        }

        System.out.println(buildSimpleSearchQuery(keywords).toString());
        var searchQueryBuilder =
            new NativeQueryBuilder().withQuery(buildSimpleSearchQuery(keywords))
                .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }
*/
public Page<IncidentsIndex> searchByVector(float[] queryVector) {
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

    var searchQuery = NativeQuery.builder()
            .withQuery((Query) knnQuery)
            .withMaxResults(1)
            .withSearchType(null)
            .build();

    try {
        var searchHitsPaged = SearchHitSupport.searchPageFor(
                elasticsearchTemplate.search(searchQuery, IncidentsIndex.class),
                searchQuery.getPageable());
        return (Page<IncidentsIndex>) SearchHitSupport.unwrapSearchHits(searchHitsPaged);
    } catch (Exception e) {
        log.error("Greška pri pretrazi u Elasticsearch-u", e);
    }

    return null;
}



/*
    @Override
    public Page<DummyIndex> advancedSearch(List<String> expression, Pageable pageable) {
        if (expression.size() != 3) {
            throw new MalformedQueryException("Search query malformed.");
        }

        String operation = expression.get(1);
        expression.remove(1);
        var searchQueryBuilder =
            new NativeQueryBuilder().withQuery(buildAdvancedSearchQuery(expression, operation))
                .withPageable(pageable);

        return runQuery(searchQueryBuilder.build());
    }
*/
@Override
public Page<IncidentsIndex> search(SearchDto searchDto, Pageable pageable) throws TranslateException, JsonProcessingException {
    if (searchDto.getSearchText().isEmpty()) {
        return Page.empty();
    }

    if (searchDto.getType().equals(SearchType.SIMPLE)) {
        List<String> tokens = new ArrayList<>(Arrays.asList(searchDto.getSearchText().split(",")));
        tokens.replaceAll(String::trim);

        // Handling specific simple search cases
        if (tokens.stream().anyMatch(token -> token.contains("employee_name"))) {
            return incidentIndexRepository.getIncidentIndicesByEmployeeNameAndSeverity(
                    tokens.get(0).split(":")[1].trim(), tokens.get(1).split(":")[1].trim(), pageable);
        } else {
            return incidentIndexRepository.getIncidentIndicesBySecurityOrganizationAndAffectedOrganization(
                    tokens.get(0).split(":")[1].trim(), tokens.get(1).split(":")[1].trim(), pageable);
        }
    } else if (searchDto.getType().equals(SearchType.KNN)) {
        return searchByVector(VectorizationUtil.getEmbedding(searchDto.getSearchText()));
    } else if (searchDto.getType().equals(SearchType.FULL)) {
        return Page.empty();  // Return empty for FULL search type
    } else {
        List<String> tokens = parseQueryString(searchDto.getSearchText());

// Generate CriteriaQuery
        Query query = buildSearchQuery(tokens);

// Perform the search on the "incidents_index"
        SearchHits<IncidentsIndex> searchHits = elasticsearchTemplate.search(query, IncidentsIndex.class);

// Process and return results
        List<IncidentsIndex> summarizedResults = searchHits.stream()
                .map(hit -> {
                    IncidentsIndex result = hit.getContent();
                    if (result != null) {
                        result = applyHighlights(hit, result);
                    }
                    return result;
                })
                .filter(Objects::nonNull)
                .toList();

        return new PageImpl<>(summarizedResults, pageable, searchHits.getTotalHits());

/*
// 2. Create NativeQuery
        NativeQuery query = NativeQuery.builder()
                .withQuery(buildSearchQuery(tokens)) // Use QueryBuilder directly
                .withPageable(pageable)
                .build();

// 3. Execute search
        SearchHits<IncidentsIndex> searchHits = elasticsearchTemplate.search(query, IncidentsIndex.class);

// 4. Process results
        List<IncidentsIndex> summarizedResults = searchHits.stream()
                .map(hit -> {
                    IncidentsIndex result = hit.getContent();
                    if (result != null) {
                        result = applyHighlights(hit, result); // Apply highlights
                    }
                    return result;
                })
                .filter(Objects::nonNull) // Ensure no nulls
                .toList();

// 5. Return paginated results
        return new PageImpl<>(summarizedResults, pageable, searchHits.getTotalHits());
*/
    }
}


    private QueryBuilder combineQueries(QueryBuilder left, QueryBuilder right, String operator) {
        switch (operator) {
            case "AND":
                return QueryBuilders.boolQuery()
                        .must(left)
                        .must(right);
            case "OR":
                return QueryBuilders.boolQuery()
                        .should(left)
                        .should(right);
            case "NOT":
                return QueryBuilders.boolQuery()
                        .mustNot(right);
            default:
                throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }

/*
    private org.springframework.data.elasticsearch.core.query.Query buildSearchQuery(List<String> tokens) {
        Stack<org.springframework.data.elasticsearch.core.query.Query> stack = new Stack<>();
        Map<String, Integer> operatorPriority = Map.of("NOT", 3, "AND", 2, "OR", 1);

        for (String token : tokens) {
            if (operatorPriority.containsKey(token)) {
                // Ensure there are enough elements in the stack for the operator
                if (token.equals("NOT")) {
                    if (stack.isEmpty()) {
                        throw new IllegalArgumentException("Invalid query: NOT operator requires one operand.");
                    }
                    org.springframework.data.elasticsearch.core.query.Query right = stack.pop();
                    stack.push((Query) combineQueries(null, (QueryBuilder) right, token));
                } else {
                    if (stack.size() < 2) {
                        throw new IllegalArgumentException("Invalid query: " + token + " operator requires two operands.");
                    }
                    org.springframework.data.elasticsearch.core.query.Query right = stack.pop();
                    org.springframework.data.elasticsearch.core.query.Query left = stack.pop();
                    stack.push((Query) combineQueries((QueryBuilder) left, (QueryBuilder) right, token));
                }
            } else {
                // Token is not an operator, treat it as a query
                String[] parts = token.split(":");
                String phrase = parts[0];
                String field = parts.length > 1 ? parts[1] : "employee_name"; // Default field "employee_name"
                stack.push(buildMatchQuery(phrase, field));
            }
        }

        // Ensure there is exactly one query in the stack after processing
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid query: unmatched operators or operands.");
        }

        return stack.pop();
    }
*/
    /*
private QueryBuilder buildSearchQuery(List<String> tokens) {
    // Define operator priority
    Map<String, Integer> operatorPriority = Map.of("NOT", 3, "AND", 2, "OR", 1);

    // Step 1: Convert infix to postfix notation
    List<String> postfixTokens = infixToPostfix(tokens, operatorPriority);

    // Step 2: Evaluate the postfix tokens to construct a BoolQuery
    QueryBuilder query = evaluatePostfix(postfixTokens);

    // Step 3: Return the query wrapped in Elasticsearch Java Client syntax
    return query;
}

*/


    private QueryBuilder buildMatchQuery(String phrase, String field) {
        return QueryBuilders.matchPhraseQuery(field, phrase);
    }




    private List<String> parseQueryString(String queryString) {
        // Podeli string po razmacima (možeš dodati i naprednije parsiranje ako je potrebno)
        return List.of(queryString.split("\\s+"));
    }


    private IncidentsIndex applyHighlights(SearchHit<IncidentsIndex> hit, IncidentsIndex result) {
        // Dinamički obradi polja sa highlight-om
        for (String field : hit.getHighlightFields().keySet()) {
            HighlightField highlight = (HighlightField) hit.getHighlightFields().get(field);
            String highlightedValue = getHighlightedText(highlight);
            // Skrati vrednost na maksimalno 10 karaktera
            highlightedValue = truncate(highlightedValue, 10);
            // Dinamički dodaj highlight - koristi refleksiju ili logiku za prilagođavanje
            result = setFieldValue(result, field, highlightedValue);
        }
        return result;
    }

    private String getHighlightedText(HighlightField highlightField) {
        StringBuilder sb = new StringBuilder();
        for (Text fragment : highlightField.fragments()) {
            sb.append(fragment.string()); // Koristi .string() da dobiješ tekst iz fragmenta
        }
        return sb.toString();
    }

    private IncidentsIndex setFieldValue(IncidentsIndex result, String fieldName, String value) {
        // Dinamički postavi vrednost polja koristeći refleksiju
        try {
            Field field = result.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(result, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Ignoriši greške za polja koja ne postoje
        }
        return result;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) return null;
        return value.length() > maxLength ? value.substring(0, maxLength) : value;
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
                // Pop until matching '('
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.add(stack.pop());
                }
                stack.pop(); // Remove '('
            } else {
                // Operand
                postfix.add(token);
            }
        }

        // Add remaining operators in stack
        while (!stack.isEmpty()) {
            postfix.add(stack.pop());
        }

        return postfix;
    }
/*
    private QueryBuilder evaluatePostfix(List<String> postfixTokens) {
        Stack<QueryBuilder> stack = new Stack<>();

        for (String token : postfixTokens) {
            if (token.equals("NOT")) {
                // Unary operator
                if (stack.isEmpty()) {
                    throw new IllegalArgumentException("Invalid query: NOT operator requires one operand.");
                }
                QueryBuilder operand = stack.pop();
                stack.push(QueryBuilders.boolQuery().mustNot(operand));
            } else if (token.equals("AND") || token.equals("OR")) {
                // Binary operator
                if (stack.size() < 2) {
                    throw new IllegalArgumentException("Invalid query: " + token + " operator requires two operands.");
                }
                QueryBuilder right = stack.pop();
                QueryBuilder left = stack.pop();
                stack.push(token.equals("AND")
                        ? QueryBuilders.boolQuery().must(left).must(right)
                        : QueryBuilders.boolQuery().should(left).should(right));
            } else {
                // Operand (e.g., employee_name:Milica)
                String[] parts = token.split(":");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid operand: " + token);
                }
                stack.push(buildMatchQuery(parts[1], parts[0])); // Adjusted order: value, field
            }
        }

        if (stack.size() != 1) {
            throw new IllegalArgumentException("Invalid query: unmatched operators or operands.");
        }

        return stack.pop();
    }
*/
private Query evaluatePostfix(List<String> postfixTokens) {
    Stack<Criteria> stack = new Stack<>();

    for (String token : postfixTokens) {
        if (token.equals("NOT")) {
            // Unary operator
            if (stack.isEmpty()) {
                throw new IllegalArgumentException("Invalid query: NOT operator requires one operand.");
            }
            Criteria operand = stack.pop();
            stack.push(new Criteria().and(operand).not());
        } else if (token.equals("AND") || token.equals("OR")) {
            // Binary operator
            if (stack.size() < 2) {
                throw new IllegalArgumentException("Invalid query: " + token + " operator requires two operands.");
            }
            Criteria right = stack.pop();
            Criteria left = stack.pop();
            stack.push(token.equals("AND") ? left.and(right) : left.or(right));
        } else {
            // Operand (e.g., employee_name:Milica)
            String[] parts = token.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid operand: " + token);
            }
            String field = parts[0];
            String value = parts[1];
            stack.push(Criteria.where(field).is(value));
        }
    }

    if (stack.size() != 1) {
        throw new IllegalArgumentException("Invalid query: unmatched operators or operands.");
    }

    // Wrap Criteria into a Query
    return new CriteriaQuery(stack.pop());
}

    private Query buildSearchQuery(List<String> tokens) {
        // Define operator priority
        Map<String, Integer> operatorPriority = Map.of("NOT", 3, "AND", 2, "OR", 1);

        // Step 1: Convert infix to postfix notation
        List<String> postfixTokens = infixToPostfix(tokens, operatorPriority);

        // Step 2: Evaluate the postfix tokens to construct a CriteriaQuery
        return evaluatePostfix(postfixTokens);
    }

}
