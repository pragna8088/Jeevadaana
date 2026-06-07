// Auto-dismiss flash alerts after 5 seconds.
document.addEventListener('DOMContentLoaded', function () {
    setTimeout(function () {
        document.querySelectorAll('.alert-dismissible').forEach(function (el) {
            const alert = bootstrap.Alert.getOrCreateInstance(el);
            alert.close();
        });
    }, 5000);

    // Set a sensible minimum (today) for camp date pickers.
    const dateInput = document.querySelector('input[type="date"]');
    if (dateInput && !dateInput.min) {
        dateInput.min = new Date().toISOString().split('T')[0];
    }
});
