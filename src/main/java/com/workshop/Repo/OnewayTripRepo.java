package com.workshop.Repo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.workshop.Entity.onewayTrip;

import jakarta.transaction.Transactional;

@Repository
public interface OnewayTripRepo extends JpaRepository<onewayTrip, Long> {

	List<Trip> findBySourceCityAndDestinationCity(String to, String from);

	Optional<onewayTrip> findById(Long id);

	List<onewayTrip> findBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
			String sourceState, String destinationState, String sourceCity, String destinationCity);

	Optional<onewayTrip> getOneWayTripPriceBySourceStateAndDestinationStateAndSourceCityAndDestinationCity(
			String sourceState, String destinationState, String sourceCity, String destinationCity);

	List<onewayTrip> findBySourceStateAndSourceCityAndDestinationStateAndDestinationCity(
			String sourceState, String sourceCity, String destinationState, String destinationCity);


			List<onewayTrip> findByEndDateBefore(LocalDate endDate);

	@Modifying
    @Transactional
    @Query("DELETE FROM onewayTrip o WHERE o.endDate < :endDate")
   public  void deleteByEndDateBefore(@Param("endDate") LocalDate endDate);


   // Method to save a list of trips
   <S extends onewayTrip> List<S> saveAll(Iterable<S> trips);

   // Method to delete all trips
   void deleteAll();

}
