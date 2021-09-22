import java.math.BigDecimal;
import java.math.RoundingMode;

public class DeliveryCostCalculator {
    public float calculate(float distance, Size size, boolean isFragile, Load load)
            throws ImpossibleDeliveryException, IllegalArgumentException {
        assumeThatCargoCanBeDelivered(distance, isFragile);

        float deliveryCost = 0;
        deliveryCost += calculateAdditionalCostDueToDistance(distance);
        deliveryCost += calculateAdditionalCostDueToSize(size);
        deliveryCost += calculateAdditionalCostDueToFragile(isFragile);
        deliveryCost *=  calculateCoefficientOfCostDueToLoad(load);

        return getFinishCost(deliveryCost);
    }

    private float calculateAdditionalCostDueToDistance(float distance) {
        if (distance > 30) {
            return 300;
        }
        if (distance >= 10) {
            return 200;
        }
        if (distance >= 2) {
            return 100;
        }
        return 50;
    }

    private float calculateAdditionalCostDueToSize(Size size) {
        switch (size) {
            case SMALL -> {
                return 100;
            }
            case BIG -> {
                return 200;
            }
            default -> throw new IllegalStateException("Unexpected size value: " + size);
        }
    }

    private float calculateAdditionalCostDueToFragile(boolean isFragile) {
        if (isFragile) {
            return 300;
        }

        return 0;
    }

    private void assumeThatCargoCanBeDelivered(float distance, boolean isFragile)
            throws ImpossibleDeliveryException, IllegalArgumentException {
        if (distance <= 0) {
            throw new IllegalArgumentException("Distance must be positive");
        }

        if (isFragile && distance > 30) {
            throw new ImpossibleDeliveryException("Fragile cargo can't be delivered more than 30 km");
        }
    }

    private float calculateCoefficientOfCostDueToLoad(Load load) {
        switch (load) {
            case NORMAL -> {
                return 1;
            }
            case ELEVATED -> {
                return 1.2f;
            }
            case HIGH -> {
                return 1.4f;
            }
            case VERY_HIGH -> {
                return 1.6f;
            }
            default -> throw new IllegalStateException("Unexpected load value: " + load);
        }
    }

    private float getFinishCost(float calculatedCost){
        float minimalCost = 400;
        if (calculatedCost <= minimalCost){
            return minimalCost;
        }

        //Rounding the calculated cost to two decimal places to avoid problems with floating-point calculations
        // (additional information in the accompanying text).
        BigDecimal bd = new BigDecimal(Float.toString(calculatedCost));
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }
}