<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${msg("login")}</title>
    <style>
        body {
            display: flex;
            min-height: 100vh;
            justify-content: center;
            align-items: center;
            background-color: #f4f4f4;
            padding-top: 2%;
            margin: 0;
        }

        .container {
            max-width: 400px;
            width: 100%;
            background: white;
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
            padding: 30px;
        }

        .logo {
            display: flex;
            justify-content: center;
            margin-bottom: 20px;
        }

        .logo img {
            height: 80px; /* Adjust height as needed */
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 500;
            color: #333;
        }

        input {
            width: 96%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        button {
            width: 100%;
            padding: 10px;
            background-color: #00204a;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        .register-link {
            text-align: center;
            margin-top: 20px;
        }

        .register-link a {
            color: #00204a;
            text-decoration: none;
        }
    </style>
    <script>
        // Function to check for error messages in the URL
        function showAlertIfLoginFailed() {
            const urlParams = new URLSearchParams(window.location.search);
            const error = urlParams.get('error');
            if (error) {
                alert("Incorrect username or password.");
            }
        }

        // Call the function on page load
        window.onload = showAlertIfLoginFailed;
    </script>
</head>
<body>
    <div class="container">
        <div class="logo">
            <img src="https://iace.tn/wp-content/uploads/2017/11/OneTech.png" alt="Your Company">
        </div>
        <form action="${url.loginAction}" method="POST" id="kc-form-login">
            <div class="form-group">
                <label for="username">${msg("username")}</label>
                <input id="username" name="username" type="text" required placeholder="Enter your username" autocomplete="username">
            </div>

            <div class="form-group">
                <label for="password">${msg("password")}</label>
                <input id="password" name="password" type="password" required placeholder="Enter your password" autocomplete="current-password">
            </div>

            <div>
                <button type="submit">${msg("Login")}</button>
            </div>
        </form>

        <div class="register-link">
            <p>${msg("noAccountYet")}</p>
            <p>Don't have an account? <a href="${url.registrationUrl}">Register here</a></p>
        </div>
    </div>
</body>
</html>
