package bank.bankApp.controller;

import bank.bankApp.Form;
import bank.bankApp.RefundForm;
import bank.bankApp.dto.OrderDto;
import bank.bankApp.dto.RefundOrderDto;
import bank.bankApp.entity.Order;
import bank.bankApp.entity.RefundOrder;
import bank.bankApp.entity.Transaction;
import bank.bankApp.exception.OrderException;
import bank.bankApp.mapper.OrderMapper;
import bank.bankApp.mapper.RefundOrderMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import bank.bankApp.repository.transactionRepository;
import bank.bankApp.repository.orderRepository;
import bank.bankApp.repository.refundOrderRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class transactionController {

//    @Resource
//    private ModelMapper modelMapper;

    @Autowired
    private transactionRepository transactionRepository;

    @Autowired
    private orderRepository orderRepository;

    @Autowired
    private refundOrderRepository refundOrderRepository;

    @PostMapping("/bankApi")
    public ResponseEntity<OrderDto> handleForm(@RequestBody OrderDto orderDto) {
        if(
                (orderDto.getOrderId() != null && !orderDto.getOrderId().equals("")) &&
                (orderDto.getAmount() != null && !orderDto.getAmount().equals("")) &&
                (orderDto.getClientId() != null && !orderDto.getClientId().equals(""))
        ) {
            Order order = OrderMapper.mapToOrder(orderDto);
            Order savedOrder = orderRepository.save(order);
            String bankUrl = "http://localhost:8081/bank/"+savedOrder.getId();
            order.setBankUrl(bankUrl);
            order.setResponse("approved");
            order.setResponseCode("00");
            OrderDto orderDto1 = OrderMapper.mapToOrderDto(savedOrder);
            orderRepository.save(order);
            return new ResponseEntity<>(orderDto1,HttpStatus.OK);
        } else {
            return null;
        }
    }

    @GetMapping("/bank/{id}")
    public ModelAndView getBank(HttpServletRequest request, @ModelAttribute Form form, @PathVariable Long id) throws Exception {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new OrderException("Order bulunamadı: " + id + "bulunamadı!")
        );
        ModelAndView mav = new ModelAndView("bank");
        mav.addObject("bankStatus",request.getParameter("status"));
        mav.addObject("order",order);
        Transaction transaction = new Transaction();
        transaction.setOrderId(order.getId());
        transactionRepository.save(transaction);
        mav.addObject("transactionId",transaction.getTransactionId());
        mav.addObject("transaction",transaction);
        return mav;
    }

    @PostMapping("/bank")
    public ModelAndView postBank(@ModelAttribute ("bankStatus") String bankStatus,
                                 @ModelAttribute Form form, HttpServletRequest request,
                                 @RequestParam("transactionId") Long transactionId) throws Exception {
        System.out.println("Transaction ID: " + transactionId);
        Transaction transaction = transactionRepository.findById(transactionId).orElseThrow(

                () -> new OrderException("Transaction ID: " + transactionId + " bulunamadı")

        );
        Map<String, String> formMap = new HashMap<>();
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldValue = (String) field.get(form);
                formMap.put(field.getName(), fieldValue);
            }
        } catch (Exception e) {
            String message = "Hata mesajı : " + e.getMessage();
            OrderException  orderException = new OrderException(message);
            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("bankStatus", "fail")
                    .addAllObjects(formMap);
        }

        Order order = orderRepository.findById(transaction.getOrderId()).orElseThrow(
                () -> new OrderException("Order bulunamadı: " + transaction.getOrderId() + "bulunamadı!")
        );

        if("accept".equals(request.getParameter("action"))){
            transaction.setTransaction_status("success");
            transaction.setTransaction_type("payment");
            transactionRepository.save(transaction);
            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("bankStatus", "succes").addAllObjects(formMap)
                    .addObject("orderId",order.getOrderId());

        }else if ("cancel".equals(request.getParameter("action"))){
            transaction.setTransaction_status("fail");
            transaction.setTransaction_type("payment");
            transactionRepository.save(transaction);
            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("bankStatus", "fail")
                    .addAllObjects(formMap)
                    .addObject("orderId",order.getOrderId());

        }else{
            transaction.setTransaction_status("pending");
            transaction.setTransaction_type("payment");
            transactionRepository.save(transaction);
            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("bankStatus", "pending")
                    .addAllObjects(formMap)
                    .addObject("orderId",order.getOrderId());
        }
    }

    @PostMapping("/bankCallBack")
    public ResponseEntity<String> bankCallBack(@RequestBody Long id) {
        if(id != null && !id.equals("")) {
            Optional<Order> orderOptional = orderRepository.findByOrderId(id);

            if (orderOptional.isPresent()) {
                Long orderId = orderOptional.get().getId();
                Optional<Transaction> optionalTransaction = transactionRepository.findByOrderId(orderId);
                if (optionalTransaction.isPresent()) {
                    String result = optionalTransaction.get().getTransaction_status();
                    return new ResponseEntity<>(result, HttpStatus.OK);
                }
            } else {
                return new ResponseEntity<>("Order not found", HttpStatus.NOT_FOUND);
            }
        }
        return null;
    }

    @PostMapping("/refundBankApi")
    public ResponseEntity<RefundOrderDto> refundBankApi(@RequestBody RefundOrderDto refundOrderDto){
        if(
                ((refundOrderDto.getOrderId() != null) || !refundOrderDto.getOrderId().equals("")) &&
                        ((refundOrderDto.getAmount() != null) || !refundOrderDto.getAmount().equals("")) &&
                        ((refundOrderDto.getTranType() != null) || !refundOrderDto.getTranType().equals("")) &&
                        ((refundOrderDto.getTotal() != null) || !refundOrderDto.getTotal().equals(""))
        ) {
            BigDecimal amount = new BigDecimal(refundOrderDto.getAmount());
            BigDecimal total = new BigDecimal(refundOrderDto.getTotal());

            if(amount.compareTo(total) < 0 || amount.compareTo(total) == 0){
                Optional<Order> order = orderRepository.findByOrderId(refundOrderDto.getOrderId());
                RefundOrder refundOrder = RefundOrderMapper.maptoRefundOrder(refundOrderDto);
                RefundOrder savedRefundOrder = refundOrderRepository.save(refundOrder);
                String bankUrl = "http://localhost:8081/bankRefund/"+savedRefundOrder.getId();
                refundOrder.setRefundBankUrl(bankUrl);
                refundOrder.setResponse("approved");
                refundOrder.setResponseCode("00");
                refundOrder.setOrderId(order.get().getId());
                refundOrder.setTotal(order.get().getAmount());
                refundOrderRepository.save(refundOrder);
                RefundOrderDto refundOrderDto1 = RefundOrderMapper.mapToRefundOrderDto(refundOrder);
                return new ResponseEntity<>(refundOrderDto1,HttpStatus.OK);
            }else{
                return new ResponseEntity<>(refundOrderDto, HttpStatus.BAD_REQUEST);
            }
        } else {
            return null;
        }
    }

    @GetMapping("/bankRefund/{id}")
    public ModelAndView getRefundBank(@ModelAttribute RefundForm refundForm, @PathVariable Long id){
        RefundOrder refundOrder = refundOrderRepository.findById(id).orElseThrow(
                () -> new OrderException("RefundOrder bulunamadı: " + id + "bulunamadı!")
        );
        Transaction transaction = new Transaction();
        transaction.setOrderId(refundOrder.getOrderId());
        transaction.setTransaction_type("refund");
        if(refundForm.getAmount() != null && !refundForm.getAmount().equals("")) {
            transaction.setTransaction_status("success");
            refundForm.setResponse("approved");
            refundForm.setResponseCode("00");
            refundOrder.setResponse("approved");
            refundOrder.setResponseCode("00");
        }else{
            transaction.setTransaction_status("fail");
            refundForm.setResponse("fail");
            refundForm.setResponseCode("99");
            refundOrder.setResponse("fail");
            refundOrder.setResponseCode("99");
        }
        transactionRepository.save(transaction);
        Order order = orderRepository.findById(refundOrder.getOrderId()).orElseThrow(
                () -> new OrderException("Order " +refundForm.getOrderId() +"bulunamadı:")
        );
        order.setAmount(order.getAmount().replace(",",".").trim());
        refundForm.setAmount(refundForm.getAmount().replace(",",".").trim());
        BigDecimal orderAmount = new BigDecimal(order.getAmount());
        BigDecimal refundAmount = new BigDecimal(refundForm.getAmount());
        BigDecimal totalAmount = orderAmount.subtract(refundAmount);

        refundForm.setTotal(totalAmount.toString());
        refundOrder.setTotal(totalAmount.toString());

        order.setAmount(totalAmount.toString());

        refundOrderRepository.save(refundOrder);
        orderRepository.save(order);
        ModelAndView mav = new ModelAndView("refund").addObject("refundForm",refundForm);
        return mav;
    }

    @PostMapping("/bankRefund")
    public ModelAndView postRefundBank(@ModelAttribute RefundForm refundForm){

        Map<String, String> formMap = new HashMap<>();
        try {
            Field[] fields = refundForm.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldValue = (String) field.get(refundForm);
                formMap.put(field.getName(), fieldValue);
            }
        } catch (Exception e) {
            String message = "Hata mesajı : " + e.getMessage();
            OrderException  orderException = new OrderException(message);
            return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                    .addAllObjects(formMap);
        }
        return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                .addAllObjects(formMap);
    }

}
