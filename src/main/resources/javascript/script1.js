let currentSlide = 0;
const slides = document.querySelectorAll('.carousel-item');
const totalSlides = slides.length;

function changeSlide(direction) {
    slides[currentSlide].classList.remove('active');
    currentSlide = (currentSlide + direction + totalSlides) % totalSlides;
    slides[currentSlide].classList.add('active');
}

function autoSlide() {
    changeSlide(1);
}

let autoSlideInterval = setInterval(autoSlide, 5000); // Change slide every 5 seconds

// Stop auto sliding when user interacts with the carousel
document.querySelector('.prev').addEventListener('click', () => {
    changeSlide(-1);
    clearInterval(autoSlideInterval);
    autoSlideInterval = setInterval(autoSlide, 5000);
});

document.querySelector('.next').addEventListener('click', () => {
    changeSlide(1);
    clearInterval(autoSlideInterval);
    autoSlideInterval = setInterval(autoSlide, 5000);
});
