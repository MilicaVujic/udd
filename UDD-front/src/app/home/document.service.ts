import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DocumentContentDto {
  EmployeeName: string;
  EmployeeSurname: string;
  SecurityOrganization: string;
  AffectedOrganization: string;
  Severity: 'NISKA' | 'SREDNJA' | 'VISOKA' | 'KRITICNA';
  AffectedOrganizationAddress: string;
}

@Injectable({
  providedIn: 'root',
})
export class DocumentService {
  private apiUrl = 'http://localhost:8080/api/index';

  constructor(private http: HttpClient) {}

  uploadDocument(file: File): Observable<DocumentContentDto> {
    const formData = new FormData();
    formData.append('multipartFile', file);
    return this.http.post<DocumentContentDto>(this.apiUrl, formData);
  }
}
