function verifyData() {
    const name = document.getElementById('name').value;
    const surname = document.getElementById('surname').value;
    const email = document.getElementById('email').value;
    const phoneNo = document.getElementById('phoneNo').value;
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    if (name == '' || surname == '' || email == '' || phoneNo == '' || username == '' || password == '') {
        alert('You must introduce all data!');
        const nameInput = document.getElementById('name');
        const surnameInput = document.getElementById('surname');
        const emailInput = document.getElementById('email');
        const phoneNoInput = document.getElementById('phoneNo');
        const usernameInput = document.getElementById('username');
        const passwordInput = document.getElementById('password');
        let nrSeconds = 0;
        const interval = setInterval(() => {
            nameInput.className = "form-control border-danger";
            surnameInput.className = "form-control border-danger";
            emailInput.className = "form-control border-danger";
            phoneNoInput.className = "form-control border-danger";
            usernameInput.className = "form-control border-danger";
            passwordInput.className = "form-control border-danger";
            ++nrSeconds;
            if (nrSeconds == 5) {
                nameInput.className = "form-control";
                surnameInput.className = "form-control";
                emailInput.className = "form-control";
                phoneNoInput.className = "form-control";
                usernameInput.className = "form-control";
                passwordInput.className = "form-control";
                clearInterval(interval);
            }
        }, 500);
    }
}
