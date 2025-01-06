const prevButton = document.getElementById('prevBtn');
const nextButton = document.getElementById('nextBtn');
const track = document.querySelector('.carousel-track');
const items = document.querySelectorAll('.carousel-item');
const itemsToShow = 3; // Number of items visible at once
let currentIndex = 0;

const moveToSlide = (index) => {
    track.style.transform = `translateX(-${index * (100 / itemsToShow)}%)`;
};

prevButton.addEventListener('click', () => {
    currentIndex = (currentIndex === 0) ? items.length - itemsToShow : currentIndex - 1;
    moveToSlide(currentIndex);
});

nextButton.addEventListener('click', () => {
    currentIndex = (currentIndex === items.length - itemsToShow) ? 0 : currentIndex + 1;
    moveToSlide(currentIndex);
});