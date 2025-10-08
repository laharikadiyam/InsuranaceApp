package com.insurance.app.management.service;

import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.catalog.MessageUtil;
import com.insurance.app.catalog.exception.ResourceNotFoundException;
import com.insurance.app.management.DTO.CarDTO;
import com.insurance.app.management.entity.Car;
import com.insurance.app.management.repository.CarRepository;
import com.insurance.app.purchase.entity.PolicyPurchase;
import com.insurance.app.purchase.repository.PolicyPurchaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final PolicyPurchaseRepository policyPurchaseRepository;
    private final MessageUtil messageUtil;

    public CarDTO addCar(CarDTO carObj) {
        Users user = userRepository.findById(carObj.getUserid())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("user.notfound", carObj.getUserid())
                ));

        Car car = convertToEntity(carObj);
        Map<String, Double> premiums = computePremiumsAndIdv(carObj);

        car.setIdv(premiums.get("idv"));
        car.setThirdPartyPremium(premiums.get("thirdPartyPremium"));
        car.setComprehensivePremium(premiums.get("comprehensivePremium"));
        car.setStatus("PENDING");
        car.setUser(user);
        car.setPurchase(null);

        return convertToDto(carRepository.save(car));
    }

    public CarDTO confirmPurchase(Long carId, PolicyPurchase purchase) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("car.notfound", carId)
                ));

        car.setPurchase(purchase);
        car.setStatus("CONFIRMED");
        return convertToDto(carRepository.save(car));
    }

    public void cancelPendingCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("car.notfound", id)
                ));

        car.setStatus("CANCELLED");
        carRepository.save(car);

        if (car.getPurchase() != null) {
            PolicyPurchase purchase = car.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }

    public void cancelCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("car.notfound", id)
                ));

        car.setStatus("CANCELLED");
        carRepository.save(car);

        if (car.getPurchase() != null) {
            PolicyPurchase purchase = car.getPurchase();
            purchase.setStatus("CANCELLED");
            policyPurchaseRepository.save(purchase);
        }
    }

    public CarDTO updatePolicy(Long id, CarDTO carObj) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageUtil.getMessage("car.details.notfound", id)
                ));

        car.setCc(carObj.getCc());
        car.setAgeinMonths(carObj.getAgeinMonths());
        car.setManufacturerName(carObj.getManufacturerName());
        car.setRegistrationNumber(carObj.getRegistrationNumber());

        Map<String, Double> premiums = computePremiumsAndIdv(carObj);
        car.setIdv(premiums.get("idv"));
        car.setThirdPartyPremium(premiums.get("thirdPartyPremium"));
        car.setComprehensivePremium(premiums.get("comprehensivePremium"));

        return convertToDto(carRepository.save(car));
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    messageUtil.getMessage("car.details.notfound", id)
            );
        }
        carRepository.deleteById(id);
    }

    public List<CarDTO> getCars() {
        return carRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private Car convertToEntity(CarDTO dto) {
        Car car = new Car();
        car.setCc(dto.getCc());
        car.setAgeinMonths(dto.getAgeinMonths());
        car.setManufacturerName(dto.getManufacturerName());
        car.setRegistrationNumber(dto.getRegistrationNumber());
        return car;
    }

    private CarDTO convertToDto(Car car) {
        CarDTO dto = new CarDTO();
        dto.setId(car.getVehicle_id());
        dto.setCc(car.getCc());
        dto.setAgeinMonths(car.getAgeinMonths());
        dto.setIdv(car.getIdv());
        dto.setManufacturerName(car.getManufacturerName());
        dto.setRegistrationNumber(car.getRegistrationNumber());
        dto.setThirdPartyPremium(car.getThirdPartyPremium());
        dto.setComprehensivePremium(car.getComprehensivePremium());
        return dto;
    }

    public Map<String, Double> computePremiumsAndIdv(CarDTO dto) {
        double baseIdv = dto.getCc() <= 1000 ? 300000 :
                dto.getCc() <= 1500 ? 500000 : 800000;

        double depreciation = baseIdv * 0.01 * dto.getAgeinMonths();
        double finalIdv = Math.max(baseIdv - depreciation, 10000);

        double thirdPartyPremium = dto.getCc() <= 1000 ? 2094 :
                dto.getCc() <= 1500 ? 3221 : 7897;

        double ownDamage = finalIdv * 0.02;
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
