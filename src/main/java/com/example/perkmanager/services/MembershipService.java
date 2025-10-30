package com.example.perkmanager.services;

import com.example.perkmanager.model.Membership;
import com.example.perkmanager.repositories.MembershipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipService(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    // Fetch all memberships
    @Transactional(readOnly = true)
    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<String> getAllMembershipTypes() {
        return membershipRepository.findAll().stream()
                .map(Membership::getType)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Fetch membership by ID
    @Transactional(readOnly = true)
    public Optional<Membership> findById(Long id) {
        return membershipRepository.findById(id);
    }

    // Create new membership
    public Membership createMembership(String type, String organizationName, String description) {
        Membership membership = new Membership(type, organizationName, description);
        return membershipRepository.save(membership);
    }
}
