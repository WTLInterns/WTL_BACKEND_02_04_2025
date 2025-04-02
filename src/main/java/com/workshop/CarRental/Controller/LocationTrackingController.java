package com.workshop.CarRental.Controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.workshop.CarRental.DTO.LocationMessage;
import com.workshop.CarRental.DTO.LocationUpdateMessage;
import com.workshop.CarRental.Entity.CarRentalUser;
import com.workshop.CarRental.Repository.CarRentalRepository;
import com.workshop.Entity.Booking;
import com.workshop.Entity.VendorDrivers;
import com.workshop.Repo.BookingRepo;
import com.workshop.Repo.VendorDriverRepo;

@Controller
public class LocationTrackingController {

    private final SimpMessagingTemplate messagingTemplate;
    private final BookingRepo bookingRepository;
    private final VendorDriverRepo vendorDriverRepository;
    private final CarRentalRepository userRepository;

    @Autowired
    public LocationTrackingController(SimpMessagingTemplate messagingTemplate,
                                    BookingRepo bookingRepository,
                                    VendorDriverRepo vendorDriverRepository,
                                    CarRentalRepository userRepository) {
        this.messagingTemplate = messagingTemplate;
        this.bookingRepository = bookingRepository;
        this.vendorDriverRepository = vendorDriverRepository;
        this.userRepository = userRepository;
    }

    @MessageMapping("/update-location")
    public void updateLocation(LocationUpdateMessage message) {
        // Validate booking exists
        Booking booking = bookingRepository.findById(message.getBookingId())
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Validate and update location based on role
        if ("DRIVER".equals(message.getRole())) {
            validateDriverBookingAssociation(booking, message.getUserId());
            updateDriverLocation(message);
        } else {
            validateUserBookingAssociation(booking, message.getUserId());
            updateUserLocation(message);
        }

        // Notify the other party
        notifyOtherParty(booking, message);
    }

    private void validateDriverBookingAssociation(Booking booking, int userId) {
        if (booking.getVendorDriver().getVendorDriverId() != userId) {
            throw new RuntimeException("Driver not associated with this booking");
        }
    }

    private void validateUserBookingAssociation(Booking booking, int userId) {
        if (booking.getCarRentalUser().getId() != userId) {
            throw new RuntimeException("User not associated with this booking");
        }
    }

    private void updateDriverLocation(LocationUpdateMessage message) {
        VendorDrivers driver = vendorDriverRepository.findById(message.getUserId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        driver.setDriverLatitude(message.getLatitude());
        driver.setDriverLongitude(message.getLongitude());
        vendorDriverRepository.save(driver);
    }

    private void updateUserLocation(LocationUpdateMessage message) {
        CarRentalUser user = userRepository.findById(message.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setUserlatitude(message.getLatitude());
        user.setUserlongitude(message.getLongitude());
        userRepository.save(user);
    }

    private void notifyOtherParty(Booking booking, LocationUpdateMessage message) {
        String destination;
        int recipientId;

        if ("DRIVER".equals(message.getRole())) {
            // Notify the user
            recipientId = booking.getCarRentalUser().getId();
            destination = "/topic/user-location/" + recipientId;
        } else {
            // Notify the driver
            recipientId = booking.getVendorDriver().getVendorDriverId();
            destination = "/topic/driver-location/" + recipientId;
        }

        messagingTemplate.convertAndSend(destination, createLocationMessage(message));
    }

    private LocationMessage createLocationMessage(LocationUpdateMessage message) {
        return new LocationMessage(
                message.getLatitude(),
                message.getLongitude(),
                message.getUserId(),
                message.getRole(),
                new Date()
        );
    }
}