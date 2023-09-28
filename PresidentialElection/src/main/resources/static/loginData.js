function verifyLoginData() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    if (username == '' || password == '') {
        let nrSeconds = 0;
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        const interval = setInterval(() => {
            usernameInput.className = "form-control border-danger";
            passwordInput.className = "form-control border-danger";
            ++nrSeconds;
            if (nrSeconds == 5) {
                usernameInput.className = "form-control";
                passwordInput.className = "form-control";
                clearInterval(interval);
            }
        }, 500);
        alert('You must introduce all data!');
    }
}