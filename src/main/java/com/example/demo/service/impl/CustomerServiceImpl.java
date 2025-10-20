package com.example.demo.service.impl;

import com.example.demo.converter.CustomerConverter;
import com.example.demo.entity.Customer;
import com.example.demo.entity.User;
import com.example.demo.model.dto.AssignmentDTO;
import com.example.demo.model.dto.CustomerDTO;
import com.example.demo.model.request.CustomerCreateRequest;
import com.example.demo.model.request.CustomerRegistrationRequest;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.model.response.StaffResponseDTO;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.CustomerService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerConverter customerConverter;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void validateRegistrationAvailability(String username, String email, String phone) {
        String normalizedUsername = StringUtils.trimToNull(username);
        if (normalizedUsername != null) {
            if (customerRepository.existsByUsername(normalizedUsername) || userRepository.findOneByUserName(normalizedUsername) != null) {
                throw new IllegalArgumentException("Tên đăng nhập đã tồn tại");
            }
        }
        String normalizedEmail = StringUtils.trimToNull(email);
        if (normalizedEmail != null) {
            if (customerRepository.existsByEmail(normalizedEmail)) {
                throw new IllegalArgumentException("Email đã tồn tại");
            }
        }
        String normalizedPhone = StringUtils.trimToNull(phone);
        if (normalizedPhone != null) {
            if (customerRepository.existsByPhone(normalizedPhone)) {
                throw new IllegalArgumentException("Số điện thoại đã tồn tại");
            }
        }
    }

    @Override
    @Transactional
    public CustomerDTO registerNewCustomer(CustomerRegistrationRequest request) {
        if (StringUtils.isBlank(request.getOtp())) {
            throw new IllegalArgumentException("Vui lòng nhập mã OTP");
        }
        if (StringUtils.isBlank(request.getPassword()) || StringUtils.isBlank(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu xác nhận không khớp");
        }
        if (StringUtils.isBlank(request.getFullname())) {
            throw new IllegalArgumentException("Họ và tên không được để trống");
        }
        if (StringUtils.isBlank(request.getUsername())) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống");
        }

        validateRegistrationAvailability(request.getUsername(), request.getEmail(), request.getPhone());

        Customer entity = new Customer();
        entity.setFullname(request.getFullname().trim());
        entity.setPhone(StringUtils.trimToNull(request.getPhone()));
        entity.setEmail(StringUtils.trimToNull(request.getEmail()));
        entity.setUsername(request.getUsername().trim());
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        entity.setStatus("Khách hàng mới");
        entity.setIsActive(1);
        entity.setDemand("");
        entity.setCompanyname("Khách hàng cá nhân");
        entity.setCreatedBy("self-register");

        Customer saved = customerRepository.save(entity);
        return customerConverter.toCustomerDTO(saved);
    }

    @Override
    public Optional<Customer> findByUsername(String username) {
        return customerRepository.findByUsername(username);
    }
    @Override
    public List<CustomerDTO> findAll(Map<String, Object> conditions) {
        List<Customer> customerEntityList = customerRepository.findAll(conditions);
        List<CustomerDTO> customerDTOS = new ArrayList<>();
        for(Customer c : customerEntityList){
            customerDTOS.add(customerConverter.toCustomerDTO(c));
        }
        return customerDTOS;
    }

//    @Override
//    public Page<CustomerDTO> findAll(Map<String, Object> conditions, int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<Customer> customerPage = customerRepository.findAll(conditions, pageable);
//        return customerPage.map(customer -> customerConverter.toCustomerDTO(customer));
//    }

    @Override
    @Transactional
    public ResponseDTO save(CustomerCreateRequest customerCreateRequest) {
        ResponseDTO responseDTO = new ResponseDTO();
        try {
            Customer customerEntity;

            // Nếu có ID → cập nhật
            if (customerCreateRequest.getId() != null) {
                Customer existing = customerRepository.findById(customerCreateRequest.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
                Customer converted = customerConverter.toCustomerEntity(customerCreateRequest);
                existing.setFullname(StringUtils.trimToEmpty(converted.getFullname()));
                existing.setPhone(StringUtils.trimToNull(converted.getPhone()));
                existing.setEmail(StringUtils.trimToNull(converted.getEmail()));
                existing.setCompanyname(converted.getCompanyname());
                existing.setDemand(converted.getDemand());
                existing.setStatus(converted.getStatus());
                if (StringUtils.isNotBlank(customerCreateRequest.getUsername())) {
                    existing.setUsername(converted.getUsername().trim());
                }
                if (StringUtils.isNotBlank(customerCreateRequest.getPassword())) {
                    existing.setPassword(passwordEncoder.encode(customerCreateRequest.getPassword()));
                }
                customerEntity = existing;
            } else {
                // Nếu là khách hàng mới
                customerEntity = customerConverter.toCustomerEntity(customerCreateRequest);
                customerEntity.setCreatedBy("web-form");
                customerEntity.setFullname(StringUtils.trimToEmpty(customerEntity.getFullname()));
                customerEntity.setPhone(StringUtils.trimToNull(customerEntity.getPhone()));
                customerEntity.setEmail(StringUtils.trimToNull(customerEntity.getEmail()));
                if (StringUtils.isNotBlank(customerEntity.getUsername())) {
                    customerEntity.setUsername(customerEntity.getUsername().trim());
                }
                if (StringUtils.isNotBlank(customerEntity.getPassword())) {
                    customerEntity.setPassword(passwordEncoder.encode(customerEntity.getPassword()));
                }
            }

            System.out.println("👉 Nhận từ form: " + customerCreateRequest.getFullname() + " - " + customerCreateRequest.getEmail());
            Customer savedCustomer = customerRepository.save(customerEntity);
            System.out.println("✅ Đã lưu khách hàng mới: " + savedCustomer.getFullname());
            responseDTO.setMessage(customerCreateRequest.getId() != null ?
                    "Cập nhật khách hàng thành công" : "Thêm khách hàng thành công");

            // Trả dữ liệu để confirm
            responseDTO.setData(customerConverter.toCustomerDTO(savedCustomer));
        } catch (Exception e) {
            e.printStackTrace();
            responseDTO.setMessage("❌ Thêm khách hàng thất bại: " + e.getMessage());
        }

        return responseDTO;
    }


    @Override
    public CustomerCreateRequest findOneById(Long id) {
        Customer customerEntity = customerRepository.findById(id).get();
        if (customerEntity != null) return customerConverter.toCustomerCreateRequest(customerEntity);
        return null;
    }

    @Override
    public ResponseDTO disableActivity(List<Long> id) {
        ResponseDTO responseDTO = new ResponseDTO();
        for(Long i : id){
            Customer customerEntity = customerRepository.findById(i).get();
            if(customerEntity != null){
                customerEntity.setIsActive(0);
                try {
                    customerRepository.save(customerEntity);
                }
                catch (Exception e){
                    System.out.println(e);
                    responseDTO.setMessage("Xóa khách hàng " + customerEntity.getId() + " thất bại");
                    return responseDTO;
                }
            }
        }
        responseDTO.setMessage("Xóa khách hàng thành công");
        return responseDTO;
    }

    @Override
    public ResponseDTO findStaffsByCustomerId(Long id) {
        Customer customerEntity = customerRepository.findById(id).get();
        List<User> staffList = userRepository.findByStatusAndRoles_Code(1, "STAFF");
        List<User> assignedStaffList = customerEntity.getUserEntities();
        List<StaffResponseDTO> staffResponseDTOS = new ArrayList<>();
        for(User u : staffList){
            StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
            staffResponseDTO.setStaffId(u.getId());
            staffResponseDTO.setFullName(u.getFullName());
            if(assignedStaffList.contains(u)){
                staffResponseDTO.setChecked("checked");
            }
            else staffResponseDTO.setChecked("");
            staffResponseDTOS.add(staffResponseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(staffResponseDTOS);
        responseDTO.setMessage("success");
        return responseDTO;
    }

    @Override
    public ResponseDTO updateAssignmentTable(AssignmentDTO assignmentDTO) {
        List<Long> staffIds = assignmentDTO.getStaffs();
        Customer customerEntity = customerRepository.findById(assignmentDTO.getId()).get();
        List<User> userEntities = new ArrayList<>();
        for(Long id : staffIds){
            userEntities.add(userRepository.findById(id).get());
        }
        customerEntity.setUserEntities(userEntities);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("success");
        return responseDTO;
    }
}
