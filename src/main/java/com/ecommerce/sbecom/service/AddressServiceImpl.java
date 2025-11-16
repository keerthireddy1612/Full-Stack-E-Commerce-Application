package com.ecommerce.sbecom.service;


import com.ecommerce.sbecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecom.model.Address;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.payload.AddressDTO;
import com.ecommerce.sbecom.repository.AddressRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public AddressDTO createAddress(User user, AddressDTO addressDTO) {
        Address address= modelMapper.map(addressDTO,Address.class);

        List<Address> addressList= user.getAddresses();
        addressList.add(address);
        user.setAddresses(addressList);

        address.setUser(user);
        Address savedAddress = addressRepository.save(address);

        return modelMapper.map(savedAddress,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getAllAddresses() {
        List<Address> addresses= addressRepository.findAll();
        List<AddressDTO> addressDTO= addresses.stream().map(address ->modelMapper.map(address,AddressDTO.class)).collect(Collectors.toList());
        return addressDTO;
    }
    @Override
    public AddressDTO getAddressById(Long addressId) {
        Address address= addressRepository.findById(addressId).orElseThrow(() ->new ResourceNotFoundException("Address", "addressId", addressId));
        return modelMapper.map(address,AddressDTO.class);
    }

    @Override
    public List<AddressDTO> getUserAddresses(User user) {
        List<Address> addresses= user.getAddresses();
        List<AddressDTO> addressDTO= addresses.stream().map(address ->modelMapper.map(address,AddressDTO.class)).collect(Collectors.toList());
        return addressDTO;
    }

    @Override
    public AddressDTO updateAddressById(Long addressId, AddressDTO addressDTO) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));

        addressFromDatabase.setCity(addressDTO.getCity());
        addressFromDatabase.setZipCode(addressDTO.getZipCode());
        addressFromDatabase.setState(addressDTO.getState());
        addressFromDatabase.setCountry(addressDTO.getCountry());
        addressFromDatabase.setStreet(addressDTO.getStreet());
        addressFromDatabase.setBuildingName(addressDTO.getBuildingName());

        Address updatedAddress = addressRepository.save(addressFromDatabase);

        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        user.getAddresses().add(updatedAddress);
        userRepository.save(user);

        return modelMapper.map(updatedAddress, AddressDTO.class);
    }

    @Override
    public String deleteAddressById(Long addressId) {
        Address addressFromDatabase = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", addressId));
        User user = addressFromDatabase.getUser();
        user.getAddresses().removeIf(address -> address.getAddressId().equals(addressId));
        addressRepository.delete(addressFromDatabase);

        return "Address with id " + addressId + " has been deleted";
    }
}
