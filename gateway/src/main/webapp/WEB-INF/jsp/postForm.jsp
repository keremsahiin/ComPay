<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Form</title>
    <script type="text/javascript" language="javascript">
        function moveWindow(){
            document.result_form.submit();
        }
    </script>
</head>
<body onload="javascript:moveWindow()">
<form:form modelAttribute="${form}" name="result_form" method="post" action="${submitUrl}" >

    <label for="response">Response:</label>
    <input type="text" id="response" name="response"  value=${form.response}><br><br>

    <label for="responseCode">Response Code:</label>
    <input type="text" id="responseCode" name="responseCode"  value=${form.responseCode}><br><br>

    <label for="clientId">Client ID:</label>
    <input type="text" id="clientId" name="clientId"  value=${form.clientId}><br><br>

    <label for="acquirerId">Acquirer ID:</label>
    <input type="text" id="acquirerId" name="acquirerId"  value=${form.acquirerId}><br><br>

    <label for="amount">Amount:</label>
    <input type="text" id="amount" name="amount" value=${form.amount}><br><br>

    <label for="currency">Currency:</label>
    <input type="text" id="currency" name="currency"  value=${form.currency}><br><br>

    <label for="shopUrl">Shop URL:</label>
    <input type="url" id="shopUrl" name="shopUrl"  value=${form.shopUrl}><br><br>

    <label for="softDescriptor">Soft Descriptor:</label>
    <input type="text" id="softDescriptor" name="softDescriptor"  value=${form.softDescriptor}><br><br>

    <label for="okUrl">OK URL:</label>
    <input type="url" id="okUrl" name="okUrl"  value=${form.okUrl}><br><br>

    <label for="failUrl">Fail URL:</label>
    <input type="url" id="failUrl" name="failUrl"  value=${form.failUrl}><br><br>

    <label for="tranType">Transaction Type:</label>
    <input type="text" id="tranType" name="tranType"  value=${form.tranType}><br><br>

    <label for="rnd">Random String:</label>
    <input type="text" id="rnd" name="rnd"  value=${form.rnd}><br><br>

    <label for="hash">Hash:</label>
    <input type="text" id="hash" name="hash" value=${form.hash}><br><br>

    <label for="hashAlgorithm">Hash Algorithm:</label>
    <input type="text" id="hashAlgorithm" name="hashAlgorithm"  value=${form.hashAlgorithm}><br><br>

</form:form>
</body>
</html>