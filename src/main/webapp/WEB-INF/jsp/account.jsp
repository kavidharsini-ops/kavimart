<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My account · kaviMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/app.css">
</head>
<body>
<main class="shell narrow">
    <nav class="nav">
        <a class="brand" href="${pageContext.request.contextPath}/">kaviMart</a>
        <a href="${pageContext.request.contextPath}/">Home</a>
    </nav>
    <section class="welcome-card">
        <p class="eyebrow">Account</p>
        <h1><c:out value="${sessionScope.authUser.name}"/></h1>
        <dl class="profile">
            <dt>Email</dt>
            <dd><c:out value="${sessionScope.authUser.email}"/></dd>
            <dt>Role</dt>
            <dd><c:out value="${sessionScope.authUser.role}"/></dd>
        </dl>
    </section>
</main>
</body>
</html>