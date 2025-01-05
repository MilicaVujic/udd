import { Component } from '@angular/core';
import { SearchContentDto, SearchService } from './search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent {
  query: string = '';
  city: string = ''; // Nova promenljiva za unos grada
  searchType: 'SIMPLE' | 'FULL' | 'KNN' | 'BOOLEAN' = 'BOOLEAN';
  simpleSearchType: 'EMPLOYEE_AND_SEVERITY' | 'SECURITY_AND_AFFECTED' | null = null;
  results: any[] = [];

  constructor(private searchService: SearchService) {}

  onSearchTypeChange() {
    if (this.searchType !== 'SIMPLE') {
      this.simpleSearchType = null; // Resetovanje opcija za SIMPLE pretragu
    }
  }

  onSimpleSearchOptionChange() {
    if (this.simpleSearchType === 'EMPLOYEE_AND_SEVERITY') {
      this.query = 'employee_name:___,severity:___';
    } else if (this.simpleSearchType === 'SECURITY_AND_AFFECTED') {
      this.query = 'security_organization:___,affected_organization:___';
    }
  }

  onSearch() {
    const searchPayload: SearchContentDto = {
      searchText: this.query,
      type: this.searchType,
      city: this.city // Dodavanje grada u SearchContentDto
    };

    this.searchService.search(searchPayload).subscribe(
      (data: any) => {
        this.results = data.content;
        console.log('Search results:', this.results);
      },
      (error) => {
        console.error('Error fetching search results', error);
      }
    );
  }

  downloadPDF(filePath: string) {
    this.searchService.downloadPDF(filePath).subscribe(
      (data: Blob) => {
        const url = window.URL.createObjectURL(data);
        const a = document.createElement('a');
        a.href = url;
        a.download = filePath.split('/').pop() || 'document.pdf';
        a.click();
        window.URL.revokeObjectURL(url);
      },
      (error) => {
        console.error('Error downloading PDF', error);
      }
    );
  }

  highlightText(text: string | undefined, query: string): string {
    if (!text) {
      return ''; // Prazan string ako nema teksta
    }

    const regex = new RegExp(query, 'gi');
    return text.replace(regex, (match) => `<mark>${match}</mark>`);
  }
}
