<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Bank Form</title>
    </head>
    <body>
        <form:form modelAttribute="${form}" name="result_form" method="post" action="http://localhost:8081/bank" >

            <input type="hidden" id="transactionId" name="transactionId" value="${transactionId}">

            <label for="transactionId">Transaction ID:</label>
            <input type="text" id="transactionId" name="transactionId" readonly value="${transactionId}"><br><br>

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
            <input type="text" id="softDescriptor" name="softDescriptor" readonly value=${form.softDescriptor}><br><br>

            <label for="okUrl">OK URL:</label>
            <input type="url" id="okUrl" name="okUrl" readonly value=${form.okUrl}><br><br>

            <label for="failUrl">Fail URL:</label>
            <input type="url" id="failUrl" name="failUrl" readonly value=${form.failUrl}><br><br>

            <label for="tranType">Transaction Type:</label>
            <input type="text" id="tranType" name="tranType" readonly value=${form.tranType}><br><br>

            <label for="rnd">Random String:</label>
            <input type="text" id="rnd" name="rnd" readonly value=${form.rnd}><br><br>

            <label for="hash">Hash:</label>
            <input type="text" id="hash" name="hash" readonly value=${form.hash}><br><br>

            <label for="hashAlgorithm">Hash Algorithm:</label>
            <input type="text" id="hashAlgorithm" name="hashAlgorithm" readonly value=${form.hashAlgorithm}><br><br>

            <input type="submit" id="accept" name="action" value="accept">

            <input type="submit" id="cancel" name="action" value="cancel">

            <input type="submit" id="pending" name="action" value="pending">

        </form:form>
    </body>
</html>