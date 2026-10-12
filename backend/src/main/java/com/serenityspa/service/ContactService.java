package com.serenityspa.service;

import com.serenityspa.dto.ContactRequest;
import com.serenityspa.dto.ContactResponse;
import com.serenityspa.entity.ContactEnquiry;
import com.serenityspa.repository.ContactEnquiryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    private final ContactEnquiryRepository contactEnquiryRepository;

    public ContactService(ContactEnquiryRepository contactEnquiryRepository) {
        this.contactEnquiryRepository = contactEnquiryRepository;
    }

    @Transactional
    public ContactResponse saveEnquiry(ContactRequest request) {
        ContactEnquiry enquiry = new ContactEnquiry(
                request.getName(),
                request.getEmail(),
                request.getPhone(),
                request.getSubject(),
                request.getMessage()
        );

        ContactEnquiry saved = contactEnquiryRepository.save(enquiry);
        return new ContactResponse(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getPhone(),
                saved.getSubject(),
                saved.getMessage(),
                saved.getCreatedAt()
        );
    }
}
