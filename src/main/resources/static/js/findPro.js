$(function() {
    /* reset 초기화 버튼*/
    $("#resetCategory").on("click", function() {
        $('#categorySelect').val("");
        let radios = document.querySelectorAll('input[type="radio"]');
        radios.forEach(function(radio) {
            radio.checked = false;
        });
    });

    /* filter 조건 상수화 */
    const filterInputs = document.querySelectorAll("#categoryFilter input");

    /* filter가 변경될 때마다 applyFilters 함수 실행 */
    filterInputs.forEach((input) => {
        input.addEventListener("change", applyFilters);
    });

    /* applyFilters 함수 */
    function applyFilters() {
        const filters = getFilters();

        fetch("/proFilter", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify(filters),
        })
            .then((response) => response.json())
            .then((pros) => updateResults(pros)) // 결과 업데이트
            .catch((error) => console.error("Error fetching experts:", error));
    }

    /* filter 조건 가져오기 */
    function getFilters() {
        const filters = {};
        const appType = document.querySelectorAll("input[type='radio']:checked");

        filters.appType = Array.from(appType).map((input) => input.value);

        return filters;
    }

    /* filter 조건에 따라 리스트 업데이트*/
    function updateResults(pros) {
        const resultContainer = document.getElementById("proListContainer");
        resultContainer.innerHTML = ""; // 기존 결과 초기화

        pros.forEach((pros) => {
            const proDiv = document.createElement("div");
            proDiv.className = "pro";
            proDiv.innerHTML =
                '<div class="col-md-12 mb-4">' +
                '  <div class="card h-100 shadow-sm">' +
                '    <div class="row card-body">' +
                '      <div class="col-10">' +
                '        <div>' +
                '          <h5 class="card-title mb-2">' + pros.member.name + '</h5>' +
                '          <span class="card-subtitle text-muted mb-2">' + pros.category.itemTitle + '</span> <br>' +
                '          <span class="badge bg-primary me-2 mb-2">' + pros.member.stackName + '</span>' +
                '          <span class="badge bg-success">' + pros.professional.career + '</span> <br>' +
                '          <span class="card-text">' + pros.professional.selfIntroduction + '</span> <br>' +
                '          <i class="bi bi-star-fill text-warning"></i>' +
                '          <span class="ms-1 mb-1">' + pros.professional.rate + '</span> <br>' +
                '          <div class="price">평균 가격: <strong>' + pros.professional.avaragePrice + '원</strong></div>' +
                '        </div>' +
                '      </div>' +
                '      <div class="col-2 align-items-center">' +
                '        <div class="row ms-1 mb-4">' +
                '          <img src="' + (pros.member.profileImage || 'default-profile.jpg') + '" class="rounded-circle" ' +
                '               style="width: 100px; height: 100px;" alt="프로필">' +
                '        </div>' +
                '        <div class="row">' +
                '          <button type="button" class="btn custom-button">견적 요청</button>' +
                '        </div>' +
                '      </div>' +
                '    </div>' +
                '  </div>' +
                '</div>';

            resultContainer.appendChild(proDiv);
        });
    }





});