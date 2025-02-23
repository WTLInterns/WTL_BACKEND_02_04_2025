package com.workshop.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.Entity.CabAdmin;
import com.workshop.Entity.DriveAdmin;
import com.workshop.Entity.OutSourceCarCab;
import com.workshop.Repo.CabAdminRepo;
import com.workshop.Repo.DriverAdminRepo;
import com.workshop.Repo.VehicleRepository;

@Service
public class CabAdminService {

	
	@Autowired
	private CabAdminRepo cabAdminRepo;
	
	// Directory where the images will be stored
    private static final String IMAGE_DIR = "src/main/resources/static/images/cabAdminImg/";

    

    // Create or Update vehicle with multiple images
    public CabAdmin saveCabAdmin(CabAdmin cabAdmin,
                                       MultipartFile vehicleRcImg,
                                       MultipartFile insurance,
                                       MultipartFile permit,
                                       MultipartFile fitnessCert,
                                       MultipartFile cabImage,
                                       MultipartFile frontImage,
                                       MultipartFile backImage,
                                       MultipartFile sideImage
                                       
                                      ) throws IOException {

        // Save images and set their file names
    	cabAdmin.setVehicleRcImg(saveImage(vehicleRcImg, "vehicleRcImg"));
    	cabAdmin.setInsurance(saveImage(insurance, "insurance"));
    	cabAdmin.setPermit(saveImage(permit, "permit"));
    	cabAdmin.setFitnessCert(saveImage(fitnessCert, "fitnessCert"));
    	cabAdmin.setCabImage(saveImage(cabImage, "cabImage"));
    	cabAdmin.setFrontImage(saveImage(frontImage, "frontImage"));
    	cabAdmin.setBackImage(saveImage(backImage, "backImage"));
    	cabAdmin.setBackImage(saveImage(sideImage, "sideImage"));

    	

        // Debugging: print the vehicle object to verify its state before saving
        System.out.println("Vehicle object to save: " + cabAdmin);

        return cabAdminRepo.save(cabAdmin); // Save the vehicle details with the image paths
    }

    // Get all vehicles
    public List<CabAdmin> getCabAdmin() {
        return cabAdminRepo.findAll();
    }

    // Get vehicle by ID
    public Optional<CabAdmin> getCabAdminById(Long id) {
        return cabAdminRepo.findById(id);
    }

//    // Delete vehicle by ID
//    public void deleteVehicle( id) {
//    	driverAdminRepo.deleteById(id);
//    }

    // Helper method to save the image and return the file name
    private String saveImage(MultipartFile image, String imageType) throws IOException {
        if (image != null && !image.isEmpty()) {
            // Create directory if not exists
            File directory = new File(IMAGE_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Get the file name, adding a timestamp to avoid collisions
            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path destinationPath = Path.of(directory.getAbsolutePath(), fileName);

            // Save the image to the file system
            Files.copy(image.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            // Log file name
            System.out.println(imageType + " saved with file name: " + fileName);

            return fileName;  // Return the saved file name
        }

        // Return a default image if no file is provided
        return "default.jpg";
    }
    
    public CabAdmin udpateStatus(Long id, String status) {
    	CabAdmin cab = this.cabAdminRepo.findById(id).orElseThrow(() -> new NoSuchElementException("Cart not found"));
    	cab.setStatus(status);
		    return cabAdminRepo.save(cab);
    }
	
	
}
