document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('form');
    const searchInput = document.getElementById('formInput');
    const imagesWrapper = document.querySelector('.images-wrapper');
    const loadingIndicator = document.getElementById('loading');
    const infoIndicator = document.getElementById('info');
    const timeValue = document.getElementById('timeValue');
    const operationTypeValue = document.getElementById('operationTypeValue');
    const nextPageButton = document.getElementById('btn-next-page');
    const prevPageButton = document.getElementById('btn-prev-page');
    const filterButton = document.getElementById('btn-filter');
    const filterPopup = document.getElementById('filter-popup');
    const closePopup = document.querySelector('.close');
    const applyFiltersButton = document.getElementById('apply-filters');
    const clearFiltersButton = document.getElementById('clear-filters');

    let currentPage = 1;
    const perPage = 10;
    let filters = {};

    filterButton.addEventListener('click', () => {
        filterPopup.style.display = 'flex';
    });

    closePopup.addEventListener('click', () => {
        filterPopup.style.display = 'none';
    });

    applyFiltersButton.addEventListener('click', () => {
        filters.orientation = document.getElementById('orientation').value;
        filters.size = document.getElementById('size').value;
        filters.color = document.getElementById('color').value;
        filters.locale = document.getElementById('locale').value;
        filters.per_page = document.getElementById('per_page').value;
        filterPopup.style.display = 'none';
    });

    clearFiltersButton.addEventListener('click', () => {
        document.getElementById('orientation').value = '';
        document.getElementById('size').value = '';
        document.getElementById('color').value = '';
        document.getElementById('locale').value = '';
        document.getElementById('per_page').value = '10';
        filters = {};
    });

    document.getElementById('btn-search').addEventListener('click', (event) => {
        event.preventDefault();
        currentPage = 1;
        const query = searchInput.value.trim();
        if (query) {
            showLoading();
            const startTime = performance.now();
            fetchImages(query, currentPage, perPage, startTime);
        }
    });

    document.getElementById('btn-clear').addEventListener('click', (event) => {
        event.preventDefault();
        searchInput.value = '';
        imagesWrapper.innerHTML = '';
        hideInfo();
    });

    nextPageButton.addEventListener('click', (event) => {
        event.preventDefault();
        const query = searchInput.value.trim();
        if (query) {
            currentPage++;
            showLoading();
            const startTime = performance.now();
            fetchImages(query, currentPage, perPage, startTime);
        }
    });

    prevPageButton.addEventListener('click', (event) => {
        event.preventDefault();
        const query = searchInput.value.trim();
        if (query && currentPage > 1) {
            currentPage--;
            showLoading();
            const startTime = performance.now();
            fetchImages(query, currentPage, perPage, startTime);
        }
    });

    function fetchImages(query, page, perPage, startTime) {
        let url = `/loadImages?query=${query}&page=${page}&perPage=${perPage}`;
        if (filters.orientation) url += `&orientation=${filters.orientation}`;
        if (filters.size) url += `&size=${filters.size}`;
        if (filters.color) url += `&color=${filters.color}`;
        if (filters.locale) url += `&locale=${filters.locale}`;
        if (filters.per_page) url += `&per_page=${filters.per_page}`;

        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                hideLoading();
                imagesWrapper.innerHTML = ''; // Önceki sonuçları temizle
                data.forEach(image => {
                    addImagesToUI(image.src.original); // Daha yüksek çözünürlüklü görsel kullan
                });
                const endTime = performance.now();
                showInfo(endTime - startTime, isComplexQuery(filters) ? 'Asenkron' : 'Senkron');
            })
            .catch((error) => {
                hideLoading();
                console.log(error);
            });
    }

    function addImagesToUI(url) {
        let div = document.createElement('div');
        let img = document.createElement('img');
        let a = document.createElement('a');

        div.className = 'image-cards';

        a.setAttribute("href", url);
        a.setAttribute("target", "_blank");

        img.setAttribute("src", url);
        img.height = 400;
        img.width = 350;
        img.style.padding = '20px';
        img.style.margin = '20px';
        img.style.border = '2px solid black';

        a.appendChild(img);
        div.appendChild(a);

        imagesWrapper.append(div);
    }

    function showLoading() {
        loadingIndicator.style.display = 'block';
    }

    function hideLoading() {
        loadingIndicator.style.display = 'none';
    }

    function showInfo(time, type) {
        timeValue.textContent = time.toFixed(2);
        operationTypeValue.textContent = type;
        infoIndicator.style.display = 'block';
    }

    function hideInfo() {
        infoIndicator.style.display = 'none';
    }

    function isComplexQuery(filters) {
        return (filters.orientation && filters.orientation !== '') ||
            (filters.size && filters.size !== '') ||
            (filters.color && filters.color !== '') ||
            (filters.locale && filters.locale !== '');
    }
});