<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Bank Form</title>
    <script type="text/javascript" language="javascript">
        function moveWindow(){
            document.refund_form.submit();
        }
    </script>
</head>
<body onload="javascript:moveWindow()">
<form:form modelAttribute="${refundForm}" name="refund_form" method="post" action="http://localhost:8081/bankRefund" >

    <label for="id">Refund ID:</label>
    <input type="text" id="id" name="id" readonly value="${id}"><br><br>

    <label for="orderId">Client ID:</label>
    <input type="text" id="orderId" name="orderId" readonly value=${refundForm.orderId}><br><br>

    <label for="total">Total:</label>
    <input type="text" id="total" name="total" readonly value=${refundForm.total}><br><br>

    <label for="amount">Amount:</label>
    <input type="text" id="amount" name="amount" readonly value=${refundForm.amount}><br><br>

    <label for="response">Response:</label>
    <input type="text" id="response" name="response" readonly value=${refundForm.response}><br><br>

    <label for="responseCode">Response Code:</label>
    <input type="text" id="responseCode" name="responseCode" readonly value=${refundForm.responseCode}><br><br>

    <label for="tranType">Transaction Type:</label>
    <input type="text" id="tranType" name="tranType" readonly value=${refundForm.tranType}><br><br>


</form:form>
</body>
</html>