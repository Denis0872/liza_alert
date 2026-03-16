package com.example.landingdemo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.landingdemo.domain.model.CaseStatus;
import com.example.landingdemo.domain.model.LostCase;
import com.example.landingdemo.domain.model.PetProfile;
import com.example.landingdemo.domain.model.PetSex;
import com.example.landingdemo.domain.model.PetSpecies;
import com.example.landingdemo.domain.model.SearchLocation;
import com.example.landingdemo.domain.model.UserAccount;
import com.example.landingdemo.domain.model.UserRole;
import com.example.landingdemo.repository.LostCaseRepository;
import com.example.landingdemo.repository.PetProfileRepository;
import com.example.landingdemo.repository.UserAccountRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class PersistenceIntegrationTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private PetProfileRepository petProfileRepository;

    @Autowired
    private LostCaseRepository lostCaseRepository;

    @Test
    void liquibaseCreatesCoreTables() {
        Integer tableCount = jdbcTemplate.queryForObject(
            """
                select count(*)
                from information_schema.tables
                where upper(table_name) in ('USER_ACCOUNT', 'PET_PROFILE', 'LOST_CASE', 'SIGHTING_REPORT', 'MEDIA_ASSET')
                """,
            Integer.class
        );

        assertThat(tableCount).isEqualTo(5);
    }

    @Test
    @Transactional
    void persistsLostCaseWithOwnerAndPetProfile() {
        UserAccount owner = new UserAccount();
        owner.setFullName("Анна Иванова");
        owner.setEmail("anna@example.com");
        owner.setPhone("+79990000000");
        owner.setRole(UserRole.OWNER);
        owner = userAccountRepository.save(owner);

        PetProfile petProfile = new PetProfile();
        petProfile.setOwner(owner);
        petProfile.setSpecies(PetSpecies.CAT);
        petProfile.setName("Нюта");
        petProfile.setSex(PetSex.FEMALE);
        petProfile.setPrimaryColor("Белый");
        petProfile.setSpecialMarks("Серое пятно на хвосте");
        petProfile = petProfileRepository.save(petProfile);

        SearchLocation location = new SearchLocation();
        location.setCity("Москва");
        location.setDistrict("Сокольники");
        location.setAddress("Русаковская улица, 10");
        location.setLatitude(new BigDecimal("55.789123"));
        location.setLongitude(new BigDecimal("37.678321"));
        location.setNotes("Скрылась у двора около арки");

        LostCase lostCase = new LostCase();
        lostCase.setPetProfile(petProfile);
        lostCase.setCreatedBy(owner);
        lostCase.setStatus(CaseStatus.ACTIVE);
        lostCase.setLostAt(Instant.parse("2026-03-15T08:30:00Z"));
        lostCase.setLastSeenLocation(location);
        lostCase.setCircumstances("Испугалась шума и выбежала из переноски.");
        lostCase.setContactPhone(owner.getPhone());
        lostCase.setUrgent(true);
        lostCaseRepository.save(lostCase);

        LostCase foundCase = lostCaseRepository.findAllByStatus(CaseStatus.ACTIVE).get(0);

        assertThat(lostCase.getId()).isNotNull();
        assertThat(lostCaseRepository.findAllByStatus(CaseStatus.ACTIVE)).hasSize(1);
        assertThat(foundCase.getPetProfile().getName()).isEqualTo("Нюта");
        assertThat(foundCase.getContactPhone()).isEqualTo("+79990000000");
    }
}
