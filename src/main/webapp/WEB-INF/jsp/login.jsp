<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Log in · kaviMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/app.css">
</head>
<body>
<main class="auth-shell">
    <a class="brand" href="${pageContext.request.contextPath}/">kaviMart</a>
    <section class="auth-card">
        <p class="eyebrow">Welcome back</p>
        <h1>Log in to your account</h1>
        <p class="muted">Continue to your marketplace account.</p>
        <form id="loginForm">
            <label for="email">Email</label>
            <input id="email" name="email" type="email" autocomplete="email" required>
            <label for="password">Password</label>
            <input id="password" name="password" type="password" autocomplete="current-password" required>
            <button class="button" type="submit">Log in</button>
            <p id="formError" class="form-error" role="alert"></p>
        </form>
        <p class="form-footer">New here? <a href="${pageContext.request.contextPath}/register">Create an account</a></p>
    </section>
</main>
<script>
    document.getElementById("loginForm").addEventListener("submit", async (event) => {
        event.preventDefault();
        const form = new FormData(event.currentTarget);
        const response = await fetch("${pageContext.request.contextPath}/api/v1/auth/login", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({email: form.get("email"), password: form.get("password")})
        });
        const body = await response.json();
        if (!body.success) {
            document.getElementById("formError").textContent = body.error.message;
            return;
        }
        window.location.href = "${pageContext.request.contextPath}/";
    });
</script>
</body>
</html>