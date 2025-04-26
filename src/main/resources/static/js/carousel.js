const prevButton = document.getElementById('prevBtn');
const nextButton = document.getElementById('nextBtn');
const track = document.querySelector('.my_carousel-track');
const items = document.querySelectorAll('.my_carousel-item');
let itemsToShow = 5; // Default number of items visible at once
let currentIndex = 0;

const moveToSlide = (index) => {
    // Update transform to move the carousel track
    track.style.transform = `translateX(-${(index * 100) / itemsToShow}%)`;
};

prevButton.addEventListener('click', () => {
    // Decrement currentIndex and wrap around when reaching the beginning
    currentIndex = (currentIndex === 0) ? Math.ceil(items.length / itemsToShow) - 1 : currentIndex - 1;
    moveToSlide(currentIndex);
});

nextButton.addEventListener('click', () => {
    // Increment currentIndex and wrap around when reaching the end
    currentIndex = (currentIndex === Math.ceil(items.length / itemsToShow) - 1) ? 0 : currentIndex + 1;
    moveToSlide(currentIndex);
});
