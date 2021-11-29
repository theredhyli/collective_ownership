import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class IntegrationTests {
    @Test
    public void integrationTest() {
        OwnershipDistribution distribution = new OwnershipDistribution(10);

        Subject firstSubject = new Subject(Arrays.asList(2,3));
        distribution.addSubject(firstSubject);
        assertDistribution(
                distribution,
                Arrays.asList(null, null, firstSubject, firstSubject, null, null, null, null, null, null),
                List.of(firstSubject)
        );

        Subject secondSubject = new Subject(Arrays.asList(2,4,5));
        distribution.addSubject(secondSubject);
        assertDistribution(
                distribution,
                Arrays.asList(null, null, firstSubject, firstSubject, secondSubject, secondSubject, null, null, null, null),
                Arrays.asList(firstSubject, secondSubject)
        );

        Subject thirdSubject = new Subject(List.of(2));
        distribution.addSubject(thirdSubject);
        assertDistribution(
                distribution,
                Arrays.asList(null, null, thirdSubject, firstSubject, secondSubject, secondSubject, null, null, null, null),
                Arrays.asList(firstSubject, secondSubject, thirdSubject)
        );

        Subject secondSubjectToRemove = new Subject(Arrays.asList(2,4,5));
        distribution.removeSubject(secondSubjectToRemove);
        assertDistribution(
                distribution,
                Arrays.asList(null, null, thirdSubject, firstSubject, null, null, null, null, null, null),
                Arrays.asList(firstSubject, thirdSubject)
        );

        Subject fourthSubject = new Subject(Arrays.asList(2,3), true);
        distribution.addSubject(fourthSubject);
        assertDistribution(
                distribution,
                Arrays.asList(null, null, thirdSubject, firstSubject, null, null, null, null, null, null),
                Arrays.asList(firstSubject, thirdSubject, fourthSubject)
        );
    }

    void assertDistribution(OwnershipDistribution distribution, List<Subject> expectedOwnership, List<Subject> expectedSubjects) {
        Assertions.assertEquals(distribution.getObjectsOwners(), expectedOwnership);
        Assertions.assertEquals(distribution.getSubjects(), expectedSubjects);
    }
}
