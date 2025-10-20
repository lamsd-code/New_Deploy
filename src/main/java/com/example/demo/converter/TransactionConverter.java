package com.example.demo.converter;

import com.example.demo.entity.Building;
import com.example.demo.entity.Customer;
import com.example.demo.entity.Transaction;
import com.example.demo.entity.User;
import com.example.demo.model.dto.TransactionDTO;
import com.example.demo.model.request.TransactionCreateRequest;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.TransactionRepository;
import com.example.demo.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TransactionConverter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private ModelMapper modelMapper;

    //Convert từ TransactionCreateRequest → Transaction Entity
    public Transaction toTransactionEntity(TransactionCreateRequest t, Long staffId) {
        Transaction transactionEntity = new Transaction();

        try {
            // 🧩 Lấy staff đang đăng nhập
            User userEntity = userRepository.findById(staffId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy staff id = " + staffId));

            // 🧩 Lấy customer
            Customer customerEntity = customerRepository.findById(t.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy customer id = " + t.getCustomerId()));
            
            // 🧩 Lấy thông tin building (nếu có)
            Building buildingEntity = null;
            if (t.getBuildingId() != null) {
                buildingEntity = buildingRepository.findById(t.getBuildingId())
                        .orElse(null);
            }

            // ⚙️ Nếu có ID → sửa
            if (t.getId() != null) {
                transactionEntity = transactionRepository.findById(t.getId())
                        .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy transaction id = " + t.getId()));

                transactionEntity.setNote(t.getNote());
                transactionEntity.setCode(t.getCode());
                transactionEntity.setType(t.getType());
                transactionEntity.setStatus(t.getStatus());
                transactionEntity.setAmount(t.getAmount());
                transactionEntity.setModifiedDate(new Date());
                transactionEntity.setModifiedBy(userEntity.getUserName());

                if (buildingEntity != null) transactionEntity.setBuilding(buildingEntity);
            }
            // 🆕 Nếu không có ID → thêm mới
            else {
                transactionEntity = modelMapper.map(t, Transaction.class);
                transactionEntity.setCustomer(customerEntity);
                transactionEntity.setStaff(userEntity);
                transactionEntity.setBuilding(buildingEntity);
                transactionEntity.setCreatedBy(userEntity.getUserName());
                transactionEntity.setCreatedDate(new Date());

                // Mặc định trạng thái PENDING nếu chưa set
                if (t.getStatus() == null || t.getStatus().isEmpty()) {
                    transactionEntity.setStatus("PENDING");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("❌ Lỗi khi convert Transaction: " + e.getMessage());
        }

        return transactionEntity;
    }

    //Convert từ Entity → DTO để trả ra frontend
    public TransactionDTO toTransactionDTO(Transaction transactionEntity) {
        return modelMapper.map(transactionEntity, TransactionDTO.class);
    }
}
