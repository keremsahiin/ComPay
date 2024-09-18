<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Form</title>
    <script type="text/javascript" language="javascript">
        function moveWindow(){
            document.refund_form.submit();
        }
    </script>
</head>
<body onload="javascript:moveWindow()">
<form:form modelAttribute="${refundForm}" name="refund_form" method="post" action="${submitUrl}" >

    <label for="response">Response:</label>
    <input type="text" id="response" name="response"  value=${refundForm.response}><br><br>

    <label for="responseCode">Response Code:</label>
    <input type="text" id="responseCode" name="responseCode"  value=${refundForm.responseCode}><br><br>

    <label for="orderId">Order ID:</label>
    <input type="text" id="orderId" name="orderId"  value=${refundForm.orderId}><br><br>

    <label for="amount">Amount:</label>
    <input type="text" id="amount" name="amount" value=${refundForm.amount}><br><br>

    <label for="total">Total:</label>
    <input type="text" id="total" name="total"  value=${refundForm.total}><br><br>

    <label for="tranType">Transaction Type:</label>
    <input type="text" id="tranType" name="tranType"  value=${refundForm.tranType}><br><br>


</form:form>
</body>
</html>