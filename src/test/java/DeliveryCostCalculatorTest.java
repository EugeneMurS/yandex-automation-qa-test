import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class DeliveryCostCalculatorTest {
    @DisplayName("cost of delivery calculated correct")
    @ParameterizedTest(name = "[{index}] delivery for {0}km (size: {1}, fragile: {2}) when system load {3} costs : {4}")
    @MethodSource("calculatePairwiseRegressionProvider")
    void calculatePairwiseRegression(float distance, Size size, boolean isFragile, Load load, float resultCost)
            throws ImpossibleDeliveryException {
        DeliveryCostCalculator calculator = new DeliveryCostCalculator();
        var actualCost = calculator.calculate(distance, size, isFragile, load);
        assertEquals(resultCost, actualCost, "Not expected delivery cost");
    }

    @Test
    void fragileCargoCantBeDeliveredMoreThan30Kilometers() {
        DeliveryCostCalculator calculator = new DeliveryCostCalculator();
        Exception exception = assertThrows(ImpossibleDeliveryException.class, () ->
                calculator.calculate(30.1f, Size.SMALL, true, Load.NORMAL));
        assertEquals("Fragile cargo can't be delivered more than 30 km", exception.getMessage());
    }

    @Test
    void distanceForDeliveryMustBePositive() {
        DeliveryCostCalculator calculator = new DeliveryCostCalculator();
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                calculator.calculate(0, Size.SMALL, true, Load.NORMAL));
        assertEquals("Distance must be positive", exception.getMessage());
    }

    static Stream<Arguments> calculatePairwiseRegressionProvider() {
        return Stream.of(
                arguments(1.5f, Size.SMALL, false, Load.NORMAL, 400),
                arguments(1.5f, Size.SMALL, false, Load.HIGH, 400),
                arguments(1.5f, Size.BIG, true, Load.ELEVATED, 660),
                arguments(1.5f, Size.BIG, true, Load.VERY_HIGH, 880),
                arguments(2, Size.SMALL, false, Load.ELEVATED, 400),
                arguments(2, Size.SMALL, true, Load.HIGH, 700),
                arguments(2, Size.BIG, false, Load.NORMAL, 400),
                arguments(2, Size.BIG, false, Load.VERY_HIGH, 480),
                arguments(5, Size.SMALL, false, Load.NORMAL, 400),
                arguments(5, Size.SMALL, true, Load.VERY_HIGH, 800),
                arguments(5, Size.BIG, false, Load.HIGH, 420),
                arguments(5, Size.BIG, true, Load.ELEVATED, 720),
                arguments(10, Size.SMALL, false, Load.VERY_HIGH, 480),
                arguments(10, Size.SMALL, true, Load.ELEVATED, 720),
                arguments(10, Size.SMALL, true, Load.HIGH, 840),
                arguments(10, Size.BIG, false, Load.NORMAL, 400),
                arguments(20, Size.SMALL, false, Load.ELEVATED, 400),
                arguments(20, Size.SMALL, false, Load.VERY_HIGH, 480),
                arguments(20, Size.SMALL, true, Load.NORMAL, 600),
                arguments(20, Size.BIG, true, Load.HIGH, 980),
                arguments(30, Size.SMALL, false, Load.NORMAL, 400),
                arguments(30, Size.SMALL, false, Load.ELEVATED, 400),
                arguments(30, Size.SMALL, false, Load.HIGH, 420),
                arguments(30, Size.SMALL, true, Load.VERY_HIGH, 960),
                arguments(30, Size.BIG, false, Load.NORMAL, 400),
                arguments(30, Size.BIG, true, Load.ELEVATED, 840),
                arguments(30, Size.BIG, true, Load.HIGH, 980),
                arguments(150, Size.SMALL, false, Load.NORMAL, 400),
                arguments(150, Size.SMALL, false, Load.ELEVATED, 480),
                arguments(150, Size.SMALL, false, Load.HIGH, 560),
                arguments(150, Size.BIG, false, Load.ELEVATED, 600),
                arguments(150, Size.BIG, false, Load.HIGH, 700),
                arguments(150, Size.BIG, false, Load.VERY_HIGH, 800)
        );
    }
}