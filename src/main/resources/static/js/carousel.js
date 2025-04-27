const prevButton = document.getElementById('prevBtn');
const nextButton = document.getElementById('nextBtn');
const track = document.querySelector('.my_carousel-track');
const items = document.querySelectorAll('.my_carousel-item');
let itemsToShow = 5; // Default number of items visible at once
let currentIndex = 0;

const moveToSlide = (index) => {
    track.style.transform = `translateX(-${(index * 100) / itemsToShow}%)`;
};

prevButton.addEventListener('click', () => {
    currentIndex = (currentIndex === 0) ? Math.ceil(items.length - itemsToShow) : currentIndex - 1;
    moveToSlide(currentIndex);
});

nextButton.addEventListener('click', () => {
    currentIndex = (currentIndex >= items.length - itemsToShow) ? 0 : currentIndex + 1;
    moveToSlide(currentIndex);
});