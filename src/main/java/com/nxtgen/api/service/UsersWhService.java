package com.nxtgen.api.service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nxtgen.api.dto.UserWhDetailResponse;
import com.nxtgen.api.entity.UsersMasterDataWhEntity;
import com.nxtgen.api.exception.UserNotFoundException;
import com.nxtgen.api.repository.UsersMasterDataWhRepository;

/**
 * Looks up users from the warehouse-style bulk dataset
 * (NXTGEN_USERS_MASTER_DATA_WH) to power the Create User form's "User ID"
 * search-and-select field.
 */
@Service
public class UsersWhService {

    private static final String USER_NOT_FOUND_MESSAGE = "Selected user record was not found.";
    private static final Pattern DIGITS_ONLY = Pattern.compile("\\d+");

    private final UsersMasterDataWhRepository usersMasterDataWhRepository;

    public UsersWhService(UsersMasterDataWhRepository usersMasterDataWhRepository) {
        this.usersMasterDataWhRepository = usersMasterDataWhRepository;
    }

    public List<UserWhDetailResponse> searchUsers(String query) {
        String trimmedQuery = Optional.ofNullable(query).map(String::trim).orElse("");

        if (trimmedQuery.isEmpty() || !DIGITS_ONLY.matcher(trimmedQuery).matches()) {
            return List.of();
        }

        return usersMasterDataWhRepository.searchByIdPrefix(trimmedQuery)
                .stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public UserWhDetailResponse fetchUserDetails(Long id) {
        UsersMasterDataWhEntity user = usersMasterDataWhRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND_MESSAGE));

        return toDetailResponse(user);
    }

    private UserWhDetailResponse toDetailResponse(UsersMasterDataWhEntity entity) {
        String emplNm = (entity.getFirstNm() + " " + entity.getLastNm()).trim();
        return new UserWhDetailResponse(entity.getId(), entity.getFirstNm(), entity.getLastNm(), emplNm, entity.getEmailAddress());
    }
}
