<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>kaviMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/app.css">
</head>
<body>
<main class="shell">
    <nav class="nav">
        <a class="brand" href="${pageContext.request.contextPath}/">kaviMart</a>
        <div class="nav-links">
            <a href="${pageContext.request.contextPath}/api/v1/health">Health API</a>
            <c:choose>
                <c:when test="${not empty sessionScope.authUser}">
                    <a href="${pageContext.request.contextPath}/account">My account</a>
                    <button type="button" id="logoutButton" class="link-button">Log out</button>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Log in</a>
                    <a class="button small" href="${pageContext.request.contextPath}/register">Create account</a>
                </c:otherwise>
            </c:choose>
        </div>
    </nav>
    <section class="hero">
        <p class="eyebrow">A marketplace for independent sellers</p>
        <h1>Find something worth bringing home.</h1>
        <p class="lede">kaviMart connects buyers with thoughtful products from sellers who care about what they make.</p>
        <div class="actions">
            <a class="button" href="${pageContext.request.contextPath}/register">Start shopping</a>
            <a class="button secondary" href="${pageContext.request.contextPath}/login">Sign in</a>
        </div>
    </section>
    <c:if test="${not empty sessionScope.authUser}">
        <section class="welcome-card">
            <p class="eyebrow">Signed in</p>
            <h2>Welcome back, <c:out value="${sessionScope.authUser.name}"/>.</h2>
            <p>Your <c:out value="${sessionScope.authUser.role}"/> account is ready. F2 seller listings will be added after authentication is verified.</p>
        </section>
    </c:if>
</main>
<script>
    const logoutButton = document.getElementById("logoutButton");
    if (logoutButton) {
        logoutButton.addEventListener("click", async () => {
            await fetch("${pageContext.request.contextPath}/api/v1/auth/logout", {method: "POST"});
            window.location.href = "${pageContext.request.contextPath}/";
        });
    }
</script>
</body>
</html>