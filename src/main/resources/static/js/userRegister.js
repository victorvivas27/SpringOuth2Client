document.addEventListener('DOMContentLoaded', function () {
    const url = "https://springouth2client-production.up.railway.app/"
    const formulario = document.querySelector("#registerForm");
    const name = document.querySelector("#nameRegister");
    const email = document.querySelector("#emailRegister");
    const picture = document.querySelector("#pictureRegister");
    const password = document.querySelector("#passwordRegister");
    
    document.getElementById("pictureRegister").addEventListener("change", function () {
        const file = this.files[0];
        const avatarPreview = document.getElementById("avatarPreview");
        
        if (file) {
            const reader = new FileReader();
            reader.onload = function (e) {
                avatarPreview.src = e.target.result;
                avatarPreview.classList.remove("hidden");
            };
            reader.readAsDataURL(file);
        } else {
            avatarPreview.src = "";
            avatarPreview.classList.add("hidden");
        }
    });

    // Asigna el evento al formulario de registro
    formulario.addEventListener("submit", function (event) {
        event.preventDefault();
        // Datos del usuario
      // Crear el objeto FormData
      const formData = new FormData();
      formData.append("name", name.value);
      formData.append("email", email.value);
      formData.append("password", password.value);
      formData.append("file", picture.files[0]); // Captura el archivo
        console.log("Datos del usuario:",formData );
        const setting = {
            method: 'POST',
           body: formData,
        };
        console.log(setting);
        realizaRegistro(setting);
    });

    function realizaRegistro(setting) {
        console.log("Lanzando la consulta a la API");
        fetch(`${url}register`, setting)
            .then(respuesta => {
                console.log("Código de respuesta:", respuesta.status);  // Ver el código de estado
                if (!respuesta.ok) {
                    // Si la respuesta no es exitosa, loguea el detalle de la respuesta
                    return respuesta.text().then(text => {
                        throw new Error(`Error: ${text || respuesta.statusText}`);
                    });
                }
                return respuesta.json();  // Procesar la respuesta JSON
            })
            .then(dato => {
                console.log("Usuario creado", dato);
                Swal.fire({
                    icon: 'success',
                    title: 'Usuario Creado',
                    showConfirmButton: false,
                    timer: 2000
                });
                // Redirige a la página de fotos después de un breve intervalo
                setTimeout(() => {
                    window.location.href = '/fotos';  // Redirigir a la página de fotos
                }, 2000);  // Coincide con el timer de Swal para asegurar que se vea el mensaje
                formulario.reset();  // Reiniciar el formulario
            })
            .catch(error => {
                console.error(`Error: ${error}`);
                mostrarError();  // Función para mostrar error (puedes definir cómo manejar los errores)
            });
    }

    function mostrarError() {
        Swal.fire({
            icon: 'error',
            title: 'Error al registrar el usuario',
            text: 'Hubo un problema al intentar crear el usuario. Por favor, inténtalo de nuevo.',
            confirmButtonText: 'Entendido'
        });
    }
});