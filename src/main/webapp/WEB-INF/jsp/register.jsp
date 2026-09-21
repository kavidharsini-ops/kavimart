<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create account · kaviMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/static/app.css">
</head>
<body>
<main class="auth-shell">
    <a class="brand" href="${pageContext.request.contextPath}/">kaviMart</a>
    <section class="auth-card">
        <p class="eyebrow">Join the marketplace</p>
        <h1>Create your account</h1>
        <p class="muted">Shop as a buyer or start selling your own products.</p>
        <form id="registerForm">
            <label for="name">Full name</label>
            <input id="name" name="name" type="text" autocomplete="name" maxlength="120" required>
            <label for="email">Email</label>
            <input id="email" name="email" type="email" autocomplete="email" required>
            <label for="password">Password</label>
            <input id="password" name="password" type="password" autocomplete="new-password" minlength="8" required>
            <label for="role">Account type</label>
            <select id="role" name="role">
                <option value="BUYER">Buyer</option>
                <option value="SELLER">Seller</option>
            </select>
            <button class="button" type="submit">Create account</button>
            <p id="formError" class="form-error" role="alert"></p>
        </form>
        <p class="form-footer">Already registered? <a href="${pageContext.request.contextPath}/login">Log in</a></p>
    </section>
</main>
<script>
    document.getElementById("registerForm").addEventListener("submit", async (event) => {
        event.preventDefault();
        const form = new FormData(event.currentTarget);
        const response = await fetch("${pageContext.request.contextPath}/api/v1/auth/register", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({
                name: form.get("name"),
                email: form.get("email"),
                password: form.get("password"),
                role: form.get("role")
            })
        });
        const body = await response.json();
        if (!body.success) {
            document.getElementById("formError").textContent = body.error.message;
            return;
        }
        window.location.href = "${pageContext.request.contextPath}/login";
    });
</script>
</body>
</html>