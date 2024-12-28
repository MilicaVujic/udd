import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { DocumentContentDto, DocumentService } from './document.service';


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  documentForm: FormGroup;
  documentData: DocumentContentDto | null = null;
  selectedFile: File | null = null;
  isFileUploaded = false;

  constructor(private fb: FormBuilder, private documentService: DocumentService) {
    this.documentForm = this.fb.group({
      employeeName: [''],
      employeeSurname: [''],
      securityOrganization: [''],
      affectedOrganization: [''],
      severity: ['NISKA'], // Default value
      affectedOrganizationAddress: [''],
    });
  }

  onFileSelected(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.files && target.files.length > 0) {
      this.selectedFile = target.files[0];
    }
  }

  onUpload(): void {
    if (this.selectedFile) {
      this.documentService.uploadDocument(this.selectedFile).subscribe((data) => {
        const formData = {
          employeeName: data.EmployeeName,
          employeeSurname: data.EmployeeSurname,
          securityOrganization: data.SecurityOrganization,
          affectedOrganization: data.AffectedOrganization,
          severity: data.Severity,
          affectedOrganizationAddress: data.AffectedOrganizationAddress,
        };
        this.documentForm.setValue(formData); // Ovo postavlja sve vrednosti
        this.isFileUploaded = true;
      });
    }
  }
  

  onSubmit(): void {
    if (this.documentForm.valid) {
      const updatedData = this.documentForm.value;
      console.log('Updated Document Data:', updatedData);
    }
  }
}
