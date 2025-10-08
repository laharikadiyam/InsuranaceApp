package com.insurance.app.management.service;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.management.DTO.BikeDTO;
import com.insurance.app.management.entity.Bike;
import com.insurance.app.management.repository.BikeRepository;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.repository.PolicyPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BikeService {

    private final BikeRepository bikeRepository;
    private final UserRepository usersRepository;
    private final PolicyPurchaseRepository policyPurchaseRepository;
    private final MessageSource messageSource;

    public BikeDTO addVehicle(BikeDTO bikeObj) {
        Users user = usersRepository.findById(bikeObj.getUserid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("user.notfound", new Object[]{bikeObj.getUserid()}, Locale.getDefault())
                ));

        Bike bike = convertToEntity(bikeObj);
        Map<String, Double> premiums = computePremiumsAndIdv(bikeObj);
        bike.setIdv(premiums.get("idv"));
        bike.setThirdPartyPremium(premiums.get("thirdPartyPremium"));
        bike.setComprehensivePremium(premiums.get("comprehensivePremium"));
        bike.setStatus("PENDING"); // new
        bike.setUser(user);
        bike.setPurchase(null);
        return convertToDto(bikeRepository.save(bike));
    }

    public BikeDTO confirmPurchase(Long bikeId, PolicyPurchase purchase) {
        Bike bike = bikeRepository.findById(bikeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("bike.notfound", new Object[]{bikeId}, Locale.getDefault())
                ));
        bike.setPurchase(purchase);
        bike.setStatus("CONFIRMED");
        return convertToDto(bikeRepository.save(bike));
    }

    public void cancelPendingBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("bike.notfound", new Object[]{id}, Locale.getDefault())
                ));

        // Update status to CANCELLED instead of deleting
        bike.setStatus("CANCELLED");
        bikeRepository.save(bike);

        // Also update the associated PolicyPurchase if it exists
        if (bike.getPurchase() != null) {
            PolicyPurchase purchase = bike.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }

    public void cancelBike(Long id) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("bike.notfound", new Object[]{id}, Locale.getDefault())
                ));

        bike.setStatus("CANCELLED");
        bikeRepository.save(bike);

        if (bike.getPurchase() != null) {
            PolicyPurchase purchase = bike.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }

    public BikeDTO updatePolicy(Long id, BikeDTO bikeObj) {
        Bike bike = bikeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageSource.getMessage("bike.details.notfound", new Object[]{id}, Locale.getDefault())
                ));

        bike.setCc(bikeObj.getCc());
        bike.setAgeinMonths(bikeObj.getAgeinMonths());
        bike.setManufacturerName(bikeObj.getManufacturerName());
        bike.setRegistrationNumber(bikeObj.getRegistrationNumber());

        Map<String, Double> premiums = computePremiumsAndIdv(bikeObj);
        bike.setIdv(premiums.get("idv"));
        bike.setThirdPartyPremium(premiums.get("thirdPartyPremium"));
        bike.setComprehensivePremium(premiums.get("comprehensivePremium"));

        return convertToDto(bikeRepository.save(bike));
    }

    public void deleteBike(Long id) {
        if (!bikeRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    messageSource.getMessage("bike.details.notfound", new Object[]{id}, Locale.getDefault())
            );
        }
        bikeRepository.deleteById(id);
    }

    public List<BikeDTO> getBikes() {
        return bikeRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Bike convertToEntity(BikeDTO bikeObj) {
        Bike bike = new Bike();
        bike.setCc(bikeObj.getCc());
        bike.setAgeinMonths(bikeObj.getAgeinMonths());
        bike.setManufacturerName(bikeObj.getManufacturerName());
        bike.setRegistrationNumber(bikeObj.getRegistrationNumber());
        return bike;
    }

    private BikeDTO convertToDto(Bike bike) {
        BikeDTO bikeObj = new BikeDTO();
        bikeObj.setVehicle_id(bike.getVehicle_id());
        bikeObj.setCc(bike.getCc());
        bikeObj.setAgeinMonths(bike.getAgeinMonths());
        bikeObj.setIdv(bike.getIdv());
        bikeObj.setManufacturerName(bike.getManufacturerName());
        bikeObj.setRegistrationNumber(bike.getRegistrationNumber());
        bikeObj.setThirdPartyPremium(bike.getThirdPartyPremium());
        bikeObj.setComprehensivePremium(bike.getComprehensivePremium());
        return bikeObj;
    }

    public Map<String, Double> computePremiumsAndIdv(BikeDTO dto) {
        Bike bike = new Bike();
        bike.setCc(dto.getCc());
        bike.setAgeinMonths(dto.getAgeinMonths());

        double baseIdv = (bike.getCc() <= 150) ? 50000 : (bike.getCc() <= 350) ? 80000 : 120000;
        double depreciation = baseIdv * 0.01 * bike.getAgeinMonths();
        double finalIdv = Math.max(baseIdv - depreciation, 10000);

        double thirdPartyPremium;
        if (bike.getCc() <= 75) {
            thirdPartyPremium = 538;
        } else if (bike.getCc() <= 150) {
            thirdPartyPremium = 714;
        } else if (bike.getCc() <= 350) {
            thirdPartyPremium = 1366;
        } else {
            thirdPartyPremium = 2804;
        }

        double ownDamage = finalIdv * 0.03;
        double gst = 0.18 * (thirdPartyPremium + ownDamage);
        double comprehensive = thirdPartyPremium + ownDamage + gst;

        Map<String, Double> map = new HashMap<>();
        map.put("idv", roundToTwoDecimal(finalIdv));
        map.put("thirdPartyPremium", roundToTwoDecimal(thirdPartyPremium));
        map.put("comprehensivePremium", roundToTwoDecimal(comprehensive));
        return map;
    }

    private double roundToTwoDecimal(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
