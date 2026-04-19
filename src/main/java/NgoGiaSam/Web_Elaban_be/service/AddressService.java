package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.AddressRepository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.dto.AddressRequest;
import NgoGiaSam.Web_Elaban_be.enity.Address;
import NgoGiaSam.Web_Elaban_be.enity.AddressType;
import NgoGiaSam.Web_Elaban_be.enity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final UserRespository userRepository;

    public List<Address> getAddressesByUser(Long userId) {
        return addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
    }

    @Transactional
    public Address createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = new Address();
        address.setUser(user);
        updateAddressFromRequest(address, request);

        // Nếu user chưa có địa chỉ nào → tự động đặt làm mặc định
        if (addressRepository.findByUserIdOrderByIsDefaultDesc(userId).isEmpty()) {
            address.setIsDefault(true);
        }

        return addressRepository.save(address);
    }

    @Transactional
    public Address updateAddress(Long addressId, Long userId, AddressRequest request) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa địa chỉ này");
        }

        updateAddressFromRequest(address, request);
        return addressRepository.save(address);
    }

    private void updateAddressFromRequest(Address address, AddressRequest request) {
        address.setFullName(request.getFullName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setDistrict(request.getDistrict());
        address.setWard(request.getWard());
        address.setAddressLine(request.getAddressLine());
        address.setNote(request.getNote());

        if (request.getAddressType() != null) {
            address.setAddressType(AddressType.valueOf(request.getAddressType().toUpperCase()));
        }

        // Xử lý isDefault
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.resetDefaultAddresses(address.getUser().getId());
            address.setIsDefault(true);
        }
    }

    @Transactional
    public void deleteAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ này");
        }

        // Nếu xóa địa chỉ mặc định → tìm địa chỉ khác làm mặc định
        if (Boolean.TRUE.equals(address.getIsDefault())) {
            List<Address> remaining = addressRepository.findByUserIdOrderByIsDefaultDesc(userId);
            remaining.removeIf(a -> a.getId().equals(addressId));

            if (!remaining.isEmpty()) {
                remaining.get(0).setIsDefault(true);
                addressRepository.save(remaining.get(0));
            }
        }

        addressRepository.delete(address);
    }

    @Transactional
    public void setDefaultAddress(Long addressId, Long userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền thực hiện thao tác này");
        }

        addressRepository.resetDefaultAddresses(userId);
        address.setIsDefault(true);
        addressRepository.save(address);
    }
}
