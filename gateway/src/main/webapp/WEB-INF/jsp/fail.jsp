<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Form</title>
</head>
<body>
    <h1>ISLEM BASARISIZ</h1>
    <form:form modelAttribute="${form}"  name="result_form" method="post" action="http://localhost:8080/fail" >
        <label for="response">Response:</label>
        <input type="text" id="response" name="response" readonly value=${form.response}><br><br>

        <label for="responseCode">Response Code:</label>
        <input type="text" id="responseCode" name="responseCode" readonly value=${form.responseCode}><br><br>

        <label for="clientId">Client ID:</label>
        <input type="text" id="clientId" name="clientId" readonly value=${form.clientId}><br><br>

        <label for="acquirerId">Acquirer ID:</label>
        <input type="text" id="acquirerId" name="acquirerId" readonly value=${form.acquirerId}><br><br>

        <label for="amount">Amount:</label>
        <input type="text" id="amount" name="amount" readonly value=${form.amount}><br><br>

        <label for="currency">Currency:</label>
        <input type="text" id="currency" name="currency" readonly value=${form.currency}><br><br>

        <label for="shopUrl">Shop URL:</label>
        <input type="url" id="shopUrl" name="shopUrl" readonly  value=${form.shopUrl}><br><br>

        <label for="softDescriptor">Soft Descriptor:</label>
        <input type="text" id="softDescriptor" name="softDescriptor" readonly  value=${form.softDescriptor}><br><br>

        <label for="okUrl">OK URL:</label>
        <input type="url" id="okUrl" name="okUrl" readonly  value=${form.okUrl}><br><br>

        <label for="failUrl">Fail URL:</label>
        <input type="url" id="failUrl" name="failUrl" readonly  value=${form.failUrl}><br><br>

        <label for="tranType">Transaction Type:</label>
        <input type="text" id="tranType" name="tranType" readonly  value=${form.tranType}><br><br>

        <label for="rnd">Random String:</label>
        <input type="text" id="rnd" name="rnd" readonly  value=${form.rnd}><br><br>

        <label for="hash">Hash:</label>
        <input type="text" id="hash" name="hash" readonly value=${form.hash}><br><br>

        <label for="hashControl">Hash Control:</label>
        <input type="text" id="hashControl" name="hashControl" readonly value=${hashControl}><br><br>

        <label for="hashAlgorithm">Hash Algorithm:</label>
        <input type="text" id="hashAlgorithm" name="hashAlgorithm"  readonly value=${form.hashAlgorithm}><br><br>

        <a id="shopUrl" href="${form.shopUrl}">Alisverise Devam Et</a>

    </form:form>
</body>
</html>