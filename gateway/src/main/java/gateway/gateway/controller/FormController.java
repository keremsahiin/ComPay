package gateway.gateway.controller;

import gateway.gateway.Form;
import gateway.gateway.RefundForm;
import gateway.gateway.dto.OrderDto;
import gateway.gateway.dto.RefundOrderDto;
import gateway.gateway.entity.Order;
import gateway.gateway.entity.RefundOrder;
import gateway.gateway.exception.OrderException;
import gateway.gateway.mapper.OrderMapper;
import gateway.gateway.mapper.RefundOrderMapper;
import gateway.gateway.repository.OrderRepository;
import gateway.gateway.repository.RefundOrderRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class FormController {

    @Autowired
    private RefundOrderRepository refundOrderRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private String generateHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest digest = MessageDigest.getInstance("SHA-512");
        digest.reset();
        digest.update(input.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(digest.digest());
    }

    private String generateAmount(String input) {
        double parsedAmount = Double.parseDouble(input);
        parsedAmount = Math.floor(parsedAmount * 100) / 100.0;
        return decimalFormat.format(parsedAmount);
    }

    private void copyProperties(@org.jetbrains.annotations.NotNull Object source, Object target) {
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Field targetField = target.getClass().getDeclaredField(field.getName());
                targetField.setAccessible(true);
                targetField.set(target, field.get(source));
            } catch (Exception e) {
                System.out.println("Hata mesajı : "+e);
            }
        }
    }

    public OrderDto sendPostRequest(String urlString, OrderDto orderdto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<OrderDto> entity = new HttpEntity<>(orderdto, headers);
        try {
            ResponseEntity<OrderDto> response = restTemplate.postForEntity(urlString, entity, OrderDto.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Hata mesajı: " + e.getMessage());
            return null;
        }
    }

    @Scheduled(cron = "0 0,15,30,45 * * * *")
    public String callBack(String urlString, Long id){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<Long> entity = new HttpEntity<>(id, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(urlString, entity, String.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Hata mesajı: " + e.getMessage());
            return null;
        }
    }

    public RefundOrderDto sendPostRefundRequest(String urlString, RefundOrderDto refundOrderDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<RefundOrderDto> entity = new HttpEntity<>(refundOrderDto, headers);
        try {
            ResponseEntity<RefundOrderDto> response = restTemplate.postForEntity(urlString, entity, RefundOrderDto.class);
            if(response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println("Hata mesajı: " + e.getMessage());
            return null;
        }
    }

    @PostMapping("/compayGateway")
    public ModelAndView compayGateway(@ModelAttribute Form form) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        if(form.getAcquirerId() == null ||
                form.getClientId() == null ||
                form.getRnd() == null ||
                form.getAmount() == null ||
                form.getHashAlgorithm() == null ||
                form.getTranType() == null ||
                form.getFailUrl() == null ||
                form.getOkUrl() == null ||
                form.getShopUrl() == null ||
                form.getCurrency() == null ||
                form.getSoftDescriptor() == null ||
                form.getHash() == null ){

            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("status", "fail");
        }else {
            if (form.getClientId().isEmpty() ||
                    form.getAcquirerId().isEmpty() ||
                    form.getAmount().isEmpty() ||
                    form.getCurrency().isEmpty() ||
                    form.getShopUrl().isEmpty() ||
                    form.getOkUrl().isEmpty() ||
                    form.getFailUrl().isEmpty() ||
                    form.getTranType().isEmpty() ||
                    form.getRnd().isEmpty() ||
                    form.getHash().isEmpty() ||
                    form.getHashAlgorithm().isEmpty()) {
                String message = "Hata mesajı : Zorunlu parametreler doldurulmalıdır!!";
                OrderException  orderException = new OrderException(message);
                return new ModelAndView("redirect:http://localhost:8080/result")
                        .addObject("status", "fail");
            } else {
                form.setAmount(generateAmount(form.getAmount()));
                SortedMap<String, String> formSorted = new TreeMap<>();
                try {
                    Field[] fields = form.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (!field.getName().equals("hash") && !field.getName().equals("storeKey")
                                && !field.getName().equals("response") && !field.getName().equals("responseCode")) {
                            String fieldValue = (String) field.get(form);
                            formSorted.put(field.getName(), fieldValue);
                        }
                    }
                } catch (Exception e) {
                    OrderException  orderException = new OrderException("Hata mesajı "+e);
                    return new ModelAndView("redirect:http://localhost:8080/result")
                            .addObject("status", "fail")
                            .addAllObjects(formSorted);
                }
                String addAnd = formSorted.values().stream()
                        .collect(Collectors.joining("|"));
                String result = addAnd + "|" + form.getStoreKey();
                String hash = generateHash(result);
                System.out.println(result);
                System.out.println(hash);
                formSorted.put("hash", hash);
                formSorted.put("responseCode", form.getResponseCode());
                formSorted.put("response", form.getResponse());

                Order order = new Order();
                copyProperties(form, order);
                Order savedOrder = orderRepository.save(order);
                OrderMapper.mapToOrderDto(savedOrder);
                OrderDto orderDto = OrderMapper.mapToOrderDto(order);

                if(form.getHash().equals(hash)){
                    try {
                        OrderDto orderDto1;
                        orderDto1 = sendPostRequest("http://localhost:8081/bankApi", orderDto);

                        if (orderDto1.getResponse().equals("approved") || orderDto1.getResponseCode().equals("00") ) {
                            return new ModelAndView("redirect:"+orderDto1.getBankUrl())
                                    .addObject("status", "success")
                                    .addAllObjects(formSorted);
                        } else {
                            return new ModelAndView("redirect:http://localhost:8080/result")
                                    .addObject("status", "fail")
                                    .addAllObjects(formSorted);
                        }
                    } catch (Exception e) {
                        OrderException  orderException = new OrderException("Hata mesajı "+e);
                        return new ModelAndView("redirect:http://localhost:8080/result")
                                .addObject("status", "fail")
                                .addAllObjects(formSorted);
                    }
                }
                else {
                        return new ModelAndView("redirect:http://localhost:8080/result")
                                .addObject("status", "fail")
                                .addAllObjects(formSorted);
                    }
                }
        }
    }

    @GetMapping("/result")
    public ModelAndView getResult(HttpServletRequest request,@ModelAttribute Form form) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if (request.getParameter("orderId") != null) {
            Order order = orderRepository.findById(Long.parseLong(request.getParameter("orderId"))).orElseThrow(
                    () -> new OrderException("Order not found")
            );
            Long id = Long.valueOf(request.getParameter("orderId"));
            String results = callBack("http://localhost:8081/bankCallBack", id);
            if (results == null) {
                ModelAndView mav = new ModelAndView("result");
                mav.addObject("status", request.getParameter("status"));
                mav.addObject("form", form);
                return mav;
            } else {
                if (form.getAcquirerId() == null ||
                        form.getClientId() == null ||
                        form.getRnd() == null ||
                        form.getAmount() == null ||
                        form.getHashAlgorithm() == null ||
                        form.getTranType() == null ||
                        form.getFailUrl() == null ||
                        form.getOkUrl() == null ||
                        form.getShopUrl() == null ||
                        form.getCurrency() == null ||
                        form.getSoftDescriptor() == null ||
                        form.getHash() == null) {
                    String message = "Hata mesajı : Parametreler null olamaz!!";
                    OrderException  orderException = new OrderException(message);
                    form.setResponse("error");
                    form.setResponseCode("99");
                    return new ModelAndView("result")
                            .addObject("status", "fail");
                } else {
                    if (form.getClientId().isEmpty() ||
                            form.getAcquirerId().isEmpty() ||
                            form.getAmount().isEmpty() ||
                            form.getCurrency().isEmpty() ||
                            form.getShopUrl().isEmpty() ||
                            form.getOkUrl().isEmpty() ||
                            form.getFailUrl().isEmpty() ||
                            form.getTranType().isEmpty() ||
                            form.getRnd().isEmpty() ||
                            form.getHash().isEmpty() ||
                            form.getHashAlgorithm().isEmpty()) {
                        form.setResponse("error");
                        form.setResponseCode("99");
                        String message = "Hata mesajı : Zorunlu parametreler doldurulmalıdır!!";
                        System.out.println(message);
                        return new ModelAndView("result")
                                .addObject("status", "fail");
                    } else {
                        if (results.equals("success")) {
                            form.setResponse("approved");
                            form.setResponseCode("00");
                        }else if (results.equals("fail")) {
                            form.setResponse("error");
                            form.setResponseCode("99");
                        }else if(results.equals("pending")){
                            results = callBack("http://localhost:8081/bankCallBack", id);
                            form.setResponse("pending");
                            form.setResponseCode("00");
                        }
                        SortedMap<String, String> formSorted = new TreeMap<>();
                        try {
                            Field[] fields = form.getClass().getDeclaredFields();
                            for (Field field : fields) {
                                field.setAccessible(true);
                                if (!field.getName().equals("hash") && !field.getName().equals("storeKey")) {
                                    String fieldValue = (String) field.get(form);
                                    formSorted.put(field.getName(), fieldValue);
                                }
                            }
                        } catch (Exception e) {
                            String message = "Hata mesajı : " + e.getMessage();
                            OrderException  orderException = new OrderException(message);
                            form.setResponse("error");
                            form.setResponseCode("99");
                            return new ModelAndView("result")
                                    .addObject("status", "fail");
                        }
                        String addAnd = formSorted.values().stream()
                                .collect(Collectors.joining("|"));
                        String result = addAnd + "|" + form.getStoreKey();
                        System.out.println(result);
                        String hash = generateHash(result);
                        form.setHash(hash);
                        System.out.println(hash);
                        formSorted.put("hash", hash);
                        if (results.equals("success")) {
                            order.setResponse("approved");
                            order.setResponseCode("00");
                            order.setHash(hash);
                            Order savedOrder = orderRepository.save(order);
                            OrderMapper.mapToOrderDto(savedOrder);
                            return new ModelAndView("result")
                                    .addObject("status", "succes")
                                    .addAllObjects(formSorted);
                        } else if(results.equals("fail")) {
                            order.setResponse("error");
                            order.setResponseCode("99");
                            order.setHash(hash);
                            Order savedOrder = orderRepository.save(order);
                            OrderMapper.mapToOrderDto(savedOrder);
                            return new ModelAndView("result")
                                    .addObject("status", "fail")
                                    .addAllObjects(formSorted);
                        }else{
                            order.setResponse("pending");
                            order.setResponseCode("00");
                            order.setHash(hash);
                            Order savedOrder = orderRepository.save(order);
                            OrderMapper.mapToOrderDto(savedOrder);
                            return new ModelAndView("result")
                                    .addObject("status", "succes")
                                    .addAllObjects(formSorted);
                        }
                    }

                }
            }
        }else {
                ModelAndView mav = new ModelAndView("result");
                mav.addObject("status", request.getParameter("status"));
                form.setResponse("error");
                form.setResponseCode("99");
                mav.addObject("form", form);
                return mav;
        }
    }
    @PostMapping("/result")
    public ModelAndView postResult(@ModelAttribute ("status") String status, @ModelAttribute Form form) {
        ModelAndView mav = new ModelAndView("postForm");
        if(status.equals("succes")){
            mav.addObject("form",form);
            mav.addObject("submitUrl",form.getOkUrl());
        }else if(status.equals("fail")){
            form.setResponse("error");
            form.setResponseCode("99");
            mav.addObject("form",form);
            mav.addObject("submitUrl",form.getFailUrl());
        }
        return mav;
    }

    @PostMapping("/succes")
    public ModelAndView succes(@ModelAttribute Form form,@ModelAttribute ("hashControl") String hashControl) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        SortedMap<String, String> formSorted = new TreeMap<>();
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.getName().equals("hash") && !field.getName().equals("storeKey")) {
                    String fieldValue = (String) field.get(form);
                    formSorted.put(field.getName(), fieldValue);
                }
            }
        } catch (Exception e) {
            new OrderException("Hata ekranı");
            form.setResponse("error");
            form.setResponseCode("99");
            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("status", "fail");
        }
        String addAnd = formSorted.values().stream()
                .collect(Collectors.joining("|"));
        String result = addAnd + "|" + form.getStoreKey();
        System.out.println(result);
        String hash = generateHash(result);
        if(form.getHash().equals(hash)){
            hashControl = "Onaylandi!";
            return new ModelAndView("succes").addObject("form",form)
                    .addObject("hashControl",hashControl);
        }else{
            hashControl = "ONAYLANMADİ!";
            return new ModelAndView("fail").addObject("form",form)
                    .addObject("hashControl",hashControl);
        }
    }

    @PostMapping("/fail")
    public ModelAndView fail(@ModelAttribute Form form,@ModelAttribute ("hashControl") String hashControl)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {
        SortedMap<String, String> formSorted = new TreeMap<>();
        try {
            Field[] fields = form.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                if (!field.getName().equals("hash") && !field.getName().equals("storeKey")) {
                    String fieldValue = (String) field.get(form);
                    formSorted.put(field.getName(), fieldValue);
                }
            }
        } catch (Exception e) {
            String message = "Hata mesajı : " + e.getMessage();
            OrderException  orderException = new OrderException(message);
            form.setResponse("error");
            form.setResponseCode("99");
            return new ModelAndView("redirect:http://localhost:8080/result")
                    .addObject("status", "fail");
        }
        String addAnd = formSorted.values().stream()
                .collect(Collectors.joining("|"));
        String result = addAnd + "|" + form.getStoreKey();
        System.out.println(result);
        String hash = generateHash(result);
        if(form.getHash().equals(hash)){
            hashControl = "Onaylandi!";
            return new ModelAndView("fail").addObject("form",form)
                    .addObject("hashControl",hashControl);
        }else{
            hashControl = "ONAYLANMADİ!";
            return new ModelAndView("fail").addObject("form",form)
                    .addObject("hashControl",hashControl);
        }
    }

    @PostMapping("/compayRefundGateway")
    public ModelAndView compayRefundGateway(@ModelAttribute RefundForm refundForm){
        if(refundForm.getAmount() == null || refundForm.getAmount().equals("") ||
                refundForm.getTranType() == null || refundForm.getTranType().equals("") ||
                refundForm.getOrderId() == null || refundForm.getOrderId().equals("")){

            String message = "Zorunlu parametreler null ve empty olamaz!";
            OrderException orderException = new OrderException(message);
            return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                    .addObject("status", "fail");

        }else{
            refundForm.setAmount(generateAmount(refundForm.getAmount()));
            Order order  = orderRepository.findById(Long.parseLong(refundForm.getOrderId())).orElseThrow(
                    () -> new OrderException("OrderId: " + refundForm.getOrderId() + "olan işlem bulunamadı")
            );
            if((order.getResponse().equals("approved") && order.getResponseCode().equals("00")) ){
                RefundOrder refundOrder = new RefundOrder();
                refundOrder.setOrderId(order.getOrderId());

                order.setAmount(order.getAmount().replace(",", ".").trim());
                refundForm.setAmount(refundForm.getAmount().replace(",", ".").trim());

                refundForm.setTotal(order.getAmount());

                BigDecimal totalAmount = new BigDecimal(refundForm.getTotal());
                BigDecimal refundAmount = new BigDecimal(refundForm.getAmount());

                copyProperties(refundForm, refundOrder);
                RefundOrder savedRefundOrder = refundOrderRepository.save(refundOrder);
                RefundOrderMapper.mapToRefundOrderDto(savedRefundOrder);
                refundForm.setRefundId(refundOrder.getRefundId());
                RefundOrderDto refundOrderDto = RefundOrderMapper.mapToRefundOrderDto(refundOrder);

                copyProperties(refundOrderDto , refundForm);
                SortedMap<String, String> refundFormSorted = new TreeMap<>();
                try {
                    Field[] fields = refundForm.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (!field.getName().equals("hash") && !field.getName().equals("storeKey")
                                && !field.getName().equals("response") && !field.getName().equals("responseCode")) {

                            if (field.getType().equals(String.class)) {
                                String fieldValue = (String) field.get(refundForm);
                                refundFormSorted.put(field.getName(), fieldValue);
                            } else {
                                Object fieldValue = field.get(refundForm);
                                refundFormSorted.put(field.getName(), fieldValue != null ? fieldValue.toString() : null);
                            }
                        }
                    }
                } catch (Exception e) {
                    OrderException orderException = new OrderException("Hata mesajı " + e);
                    return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                            .addObject("status", "fail")
                            .addAllObjects(refundFormSorted);
                }

                if((order.getOrderId().equals(refundForm.getOrderId()) || order.getTranType().equals(refundForm.getTranType())) &&
                        (refundAmount.compareTo(totalAmount) == 0 || refundAmount.compareTo(totalAmount) < 0) ) {

                    RefundOrderDto refundOrderDto1;
                    refundOrderDto1 = sendPostRefundRequest("http://localhost:8081/refundBankApi",refundOrderDto);

                    if(refundOrderDto1.getResponseCode().equals("00") || refundOrderDto1.getResponse().equals("approved")){
                        return new ModelAndView("redirect:"+refundOrderDto1.getRefundBankUrl())
                                .addAllObjects(refundFormSorted)
                                .addObject("status", "success");
                    }
                    return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                            .addObject("status", "fail")
                            .addAllObjects(refundFormSorted);
                }else {
                    return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                            .addObject("status", "fail")
                            .addAllObjects(refundFormSorted);
                }
            }else if((order.getResponse().equals("error") && order.getResponseCode().equals("99"))){
                SortedMap<String, String> refundFormSorted = new TreeMap<>();
                try {
                    Field[] fields = refundForm.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (!field.getName().equals("hash") && !field.getName().equals("storeKey")
                                && !field.getName().equals("response") && !field.getName().equals("responseCode")) {

                            if (field.getType().equals(String.class)) {
                                String fieldValue = (String) field.get(refundForm);
                                refundFormSorted.put(field.getName(), fieldValue);
                            } else {
                                Object fieldValue = field.get(refundForm);
                                refundFormSorted.put(field.getName(), fieldValue != null ? fieldValue.toString() : null);
                            }
                        }
                    }
                } catch (Exception e) {
                    OrderException orderException = new OrderException("Hata mesajı " + e);
                    return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                            .addObject("status", "fail")
                            .addAllObjects(refundFormSorted);
                }
                return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                        .addObject("status", "fail")
                        .addAllObjects(refundFormSorted);
            }else{
                SortedMap<String, String> refundFormSorted = new TreeMap<>();
                try {
                    Field[] fields = refundForm.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (!field.getName().equals("hash") && !field.getName().equals("storeKey")
                                && !field.getName().equals("response") && !field.getName().equals("responseCode")) {

                            if (field.getType().equals(String.class)) {
                                String fieldValue = (String) field.get(refundForm);
                                refundFormSorted.put(field.getName(), fieldValue);
                            } else {
                                Object fieldValue = field.get(refundForm);
                                refundFormSorted.put(field.getName(), fieldValue != null ? fieldValue.toString() : null);
                            }
                        }
                    }
                } catch (Exception e) {
                    OrderException orderException = new OrderException("Hata mesajı " + e);
                    return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                            .addObject("status", "fail")
                            .addAllObjects(refundFormSorted);
                }
                return new ModelAndView("redirect:http://localhost:8080/resultRefund")
                        .addObject("status", "fail")
                        .addAllObjects(refundFormSorted);
            }

        }
    }

    @GetMapping("/resultRefund")
    public ModelAndView getResultRefund(@ModelAttribute RefundForm refundForm){
        refundForm.setAmount(refundForm.getAmount());
        Order order = orderRepository.findById(Long.parseLong(refundForm.getOrderId())).orElseThrow(
                () -> new OrderException("Bulunamadı!")
        );
        if(refundForm.getTotal() != null){
            order.setAmount(refundForm.getTotal());
            orderRepository.save(order);
        }else{
         refundForm.setTotal(order.getAmount());
        }
        if(
                (refundForm.getOrderId() == null || refundForm.getOrderId().equals("")) &&
                (refundForm.getTranType() == null || refundForm.getTranType().equals("")) &&
                (refundForm.getAmount() == null || refundForm.getAmount().equals("")) &&
                (refundForm.getTotal() == null || refundForm.getTotal().equals(""))
        ){
            String message = "Zorunlu parametreler null ve empty olamaz!";
            throw new OrderException(message);
        }
        if(refundForm.getResponseCode().equals("00") && refundForm.getResponse().equals("approved")){
            return new ModelAndView("resultRefund")
                    .addObject("refundForm",refundForm)
                    .addObject("status", "success");
        }else{
            return new ModelAndView("resultRefund")
                    .addObject("refundForm",refundForm)
                    .addObject("status", "fail");
        }
    }

    @PostMapping("/resultRefund")
    public ModelAndView postResultRefund(@ModelAttribute RefundForm refundForm,@ModelAttribute ("status") String status){
        ModelAndView mav = new ModelAndView("refundPostForm");
        if(status.equals("success")){
            mav.addObject("refundForm",refundForm);
            mav.addObject("submitUrl","http://localhost:8080/successRefund");
        }else if(status.equals("fail")){
            refundForm.setResponse("error");
            refundForm.setResponseCode("99");
            mav.addObject("refundForm",refundForm);
            mav.addObject("submitUrl","http://localhost:8080/failRefund");
        }
        return mav;
    }

    @PostMapping("/successRefund")
    public ModelAndView successRefund(@ModelAttribute RefundForm refundForm){
         return new ModelAndView("successRefund").addObject("refundForm",refundForm);
    }

    @PostMapping("/failRefund")
    public ModelAndView failRefund(@ModelAttribute RefundForm refundForm){
            return new ModelAndView("failRefund").addObject("refundForm",refundForm);
    }

}
