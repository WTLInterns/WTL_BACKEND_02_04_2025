package com.workshop.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.workshop.Entity.DriveAdmin;
import com.workshop.Entity.OutSourceCarCab;
import com.workshop.Repo.DriverAdminRepo;
import com.workshop.Repo.VehicleRepository;

@Service
public class DriverAdminService {

	
	@Autowired
	private DriverAdminRepo driverAdminRepo;
	
	// Directory where the images will be stored
    private static final String IMAGE_DIR = "src/main/resources/static/images/driverAdminImg/";

    

    // Create or Update vehicle with multiple images
    public DriveAdmin saveDriverAdmin(DriveAdmin driveAdmin,
                                       MultipartFile DriverImgSelfie,
                                       MultipartFile Aadhar,
                                       MultipartFile DrLicenceNum,
                                       MultipartFile PvcNo
                                      ) throws IOException {

        // Save images and set their file names
    	driveAdmin.setDriverImgSelfie(saveImage(DriverImgSelfie, "DriverImgSelfie"));
    	driveAdmin.setAadhar(saveImage(Aadhar, "Aadhar"));
    	driveAdmin.setDrLicenceNum(saveImage(DrLicenceNum, "DrLicenceNum"));
    	driveAdmin.setPvcNo(saveImage(PvcNo, "PvcNo"));
    	
    	

        // Debugging: print the vehicle object to verify its state before saving
        System.out.println("Vehicle object to save: " + driveAdmin);

        return driverAdminRepo.save(driveAdmin); // Save the vehicle details with the image paths
    }

    // Get all vehicles
    public List<DriveAdmin> getDriverAdmin() {
        return driverAdminRepo.findAll();
    }

    // Get vehicle by ID
    public Optional<DriveAdmin> getDriverAdminById(int id) {
        return driverAdminRepo.findById(id);
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
	
	
}
