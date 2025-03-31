package accounts.web;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import rewards.internal.restaurant.RestaurantRepository;

@Component
public class RestaurantHealthCheck implements HealthIndicator {

    RestaurantRepository restaurantRepository;

    RestaurantHealthCheck(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public Health health() {
        if (restaurantRepository.getRestaurantCount().equals(0L)) {
            return Health.down().build();
        } else {
            return Health.up().build();
        }
    }

}
