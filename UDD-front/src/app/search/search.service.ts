import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';


export interface SearchContentDto {
  searchText: string;
  type: 'SIMPLE'|'KNN'|'BOOLEAN'|'FULL';
  city:string;
}


@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  search(query: SearchContentDto): Observable<any[]> {
    return this.http.post<any[]>(`${this.baseUrl}/search`, query);
  }

  downloadPDF(id: string): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/file/${id}`, {
      responseType: 'blob'
    });
  }
}
