document.addEventListener("DOMContentLoaded", function() {
	// 프로필 이미지 변경 및 미리보기
	const profileImageInput = document.getElementById('profileImage');
	const previewImage = document.getElementById('previewImage');
	let uploadedImage = null;

	if (profileImageInput) {
		profileImageInput.addEventListener('change', function (e) {
			const file = e.target.files[0];
			if (file) {
				// 미리보기 표시
				const reader = new FileReader();
				reader.onload = function (e) {
					previewImage.src = e.target.result;
				}
				reader.readAsDataURL(file);
				uploadedImage = file;
			}
		});
	}

	document.getElementById('memberUpdateForm').addEventListener('submit', function (e) {
		e.preventDefault();

		const formData = new FormData(this);

		// 이미지가 변경되었을 경우만 FormData에 추가
		if (uploadedImage) {
			formData.set('profileImage', uploadedImage);
		}

		$.ajax({
			url: '/updateMember',
			type: 'POST',
			data: formData,
			processData: false,
			contentType: false,
			success: function (response) {
				if (response && response.success) {
					alert(response.message);
					window.location.href = '/main';
				} else {
					alert(response.message || '회원 정보 수정에 실패했습니다.');
					window.location.href = '/updateMemberForm';
				}
			},
			error: function (xhr, status, error) {
				console.error('Error:', error);
				alert('회원 정보 수정 중 오류가 발생했습니다.');
				window.location.href = '/updateMemberForm';
			}
		});
	});

	// 각 필드의 유효성 상태 추적
	const validationState = {
		name: false,
		memberId: false,
		pass: false,
		passwordMatch: false,
		nickname: false,
		email: false,
		phone: false,
		birth: false,
		gender: false,
		address: false
	};

	// 비밀번호 확인 폼 제출 시
	$('#passwordCheckForm').on('submit', function (e) {
		e.preventDefault();
		const password = $('#currentPassword').val();

		$.ajax({
			url: '/ajax/member/checkPassword',
			type: 'POST',
			data: {pass: password},
			success: function (response) {
				if (response.valid) {
					$('#passwordCheckModal').modal('hide');
					// 회원 정보 가져와서 프로필 페이지 표시
					$.ajax({
						url: '/ajax/member/getMemberInfo',
						type: 'GET',
						success: function (memberData) {
							$('.content-container').html(generateProfileHTML(memberData));
							// 폼이 로드된 후 표시 애니메이션 적용
							$('.profile-form-container').fadeIn();
						},
						error: function () {
							alert('회원정보를 불러오는데 실패했습니다.');
						}
					});
				} else {
					$('#passwordError').text('비밀번호가 일치하지 않습니다.');
				}
			},
			error: function () {
				$('#passwordError').text('서버 오류가 발생했습니다.');
			}
		});
	});

	// 프로필 이미지 변경 시 미리보기
	$(document).on('change', '#profileImage', function (e) {
		const file = e.target.files[0];
		if (file) {
			const reader = new FileReader();
			reader.onload = function (e) {
				$('.profile-image-container').attr('src', e.target.result);
			}
			reader.readAsDataURL(file);
		}
	});


	// 다음 지도 API
	$(document).on('click', '#addressSearchBtn2', function () {
		console.log('주소 찾기 버튼 클릭됨');  // 버튼 클릭 로그

		if (typeof daum === 'undefined') {
			console.error('Daum 우편번호 서비스 로드 실패');
			alert('주소 검색 서비스를 불러오는데 실패했습니다.');
			return;
		}
		if (profileImageInput) {
			profileImageInput.addEventListener('change', function (e) {
				const file = e.target.files[0];
				if (file) {
					const reader = new FileReader();
					reader.onload = function (e) {
						document.querySelector('.profile-image-container').src = e.target.result;
					}
					reader.readAsDataURL(file);
				}
			});
		}
	});


		// 모달이 자동으로 열리지 않도록 초기화
		$('#changePasswordModalShow').modal('hide'); // 페이지 로드 시 모달 숨기기

		// 비밀번호 변경 버튼 클릭 시 모달 열기
		$('#changePasswordBtnShow').on('click', function () {
			$('#changePasswordModalShow').modal('show');
		});


		// 비밀번호 유효성 검사 패턴
		const patterns = {
			password: /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,16}$/
		};

		// 비밀번호 변경 버튼 클릭 시 모달 오픈
		$(document).on('click', '#changePasswordBtnShow', function () {
			$('#changePasswordModalShow').modal('show');
		});

		// 새 비밀번호 유효성 검사
		$(document).on('input', '#newPasswordShow', function () {
			const password = $(this).val().trim();
			let isValid = patterns.password.test(password);

			if (isValid) {
				$(this).removeClass('is-invalid').addClass('is-valid');
				$('#newPasswordErrorShow').text('').hide();
			} else {
				$(this).removeClass('is-valid').addClass('is-invalid');
				$('#newPasswordErrorShow').text('비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.').show();
			}

			validatePasswordConfirmation();
		});

		// 비밀번호 확인 검증
		$(document).on('input', '#confirmPasswordShow', function () {
			validatePasswordConfirmation();
		});

		function validatePasswordConfirmation() {
			const currentPassword = $('#currentPasswordShow').val().trim();
			const newPassword = $('#newPasswordShow').val().trim();
			const confirmPassword = $('#confirmPasswordShow').val().trim();

			const isCurrentPasswordFilled = currentPassword.length > 0;
			const isNewPasswordValid = patterns.password.test(newPassword);
			const isPasswordMatching = newPassword === confirmPassword;

			// 모든 조건 확인
			if (isCurrentPasswordFilled && isNewPasswordValid && isPasswordMatching) {
				$('#currentPasswordShow').removeClass('is-invalid').addClass('is-valid');
				$('#newPasswordShow').removeClass('is-invalid').addClass('is-valid');
				$('#confirmPasswordShow').removeClass('is-invalid').addClass('is-valid');

				$('#currentPasswordErrorShow, #newPasswordErrorShow, #confirmPasswordErrorShow')
					.text('').hide();

				$('#savePasswordBtnShow').prop('disabled', false);
			} else {
				// 각 입력 필드에 대한 개별 검증
				if (!isCurrentPasswordFilled) {
					$('#currentPasswordShow').addClass('is-invalid');
					$('#currentPasswordErrorShow').text('현재 비밀번호를 입력해주세요.').show();
				}

				if (!isNewPasswordValid) {
					$('#newPasswordShow').addClass('is-invalid');
					$('#newPasswordErrorShow').text('비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.').show();
				}

				if (!isPasswordMatching) {
					$('#confirmPasswordShow').addClass('is-invalid');
					$('#confirmPasswordErrorShow').text('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.').show();
				}

				$('#savePasswordBtnShow').prop('disabled', true);
			}
		}

// 각 입력 필드에 이벤트 리스너 추가
		$(document).on('input', '#currentPasswordShow, #newPasswordShow, #confirmPasswordShow', function () {
			validatePasswordConfirmation();
		});

		// 비밀번호 변경 버튼 클릭 이벤트
		$(document).on('click', '#savePasswordBtnShow', function (e) {
			e.preventDefault();

			const currentPassword = $('#currentPasswordShow').val().trim();
			const newPassword = $('#newPasswordShow').val().trim();
			const confirmPassword = $('#confirmPasswordShow').val().trim();

			// 모든 필드 검증
			if (!currentPassword || !newPassword || !confirmPassword) {
				alert('모든 필드를 입력해주세요.');
				return;
			}

			// 새 비밀번호 유효성 검사
			if (!patterns.password.test(newPassword)) {
				alert('새 비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.');
				return;
			}

			// 새 비밀번호 확인 검사
			if (newPassword !== confirmPassword) {
				alert('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
				return;
			}

			// 서버에 비밀번호 변경 요청
			$.ajax({
				url: '/ajax/member/changePassword',
				type: 'POST',
				contentType: 'application/json',
				data: JSON.stringify({
					currentPasswordShow: currentPassword,
					newPasswordShow: newPassword,
					confirmPasswordShow: confirmPassword
				}),
				success: function (response) {
					alert(response.message);
					$('#changePasswordModalShow').modal('hide');
					// 모달 초기화
					$('#currentPasswordShow, #newPasswordShow, #confirmPasswordShow').val('');
					$('#currentPasswordShow, #newPasswordShow, #confirmPasswordShow')
						.removeClass('is-valid is-invalid');
					$('#newPasswordErrorShow, #confirmPasswordErrorShow').text('').hide();
				},
				error: function (xhr) {
					const errorMessage = xhr.responseJSON ?
						xhr.responseJSON.message :
						'비밀번호 변경 중 오류가 발생했습니다.';
					alert(errorMessage);
				}
			});
		});

		// 모달 닫힐 때 초기화
		$('#changePasswordModalShow').on('hidden.bs.modal', function () {
			// 입력 필드 초기화
			$('#currentPasswordShow, #newPasswordShow, #confirmPasswordShow').val('');

			// 유효성 검사 클래스 제거
			$('#currentPasswordShow, #newPasswordShow, #confirmPasswordShow')
				.removeClass('is-valid is-invalid');

			// 에러 메시지 숨기기
			$('#newPasswordErrorShow, #confirmPasswordErrorShow').text('').hide();

			// 저장 버튼 비활성화
			$('#savePasswordBtnShow').prop('disabled', true);
		});

		// 매칭 처리 함수
		function handleMatching(estimationNo) {
			if (confirm('해당 견적을 매칭하시겠습니까?')) {
				$.ajax({
					url: '/ajax/member/matchEstimation',
					type: 'POST',
					data: {estimationNo: estimationNo},
					success: function (response) {
						alert('매칭이 완료되었습니다.');
						// 견적 목록 새로고침
						loadContent('Request');
					},
					error: function () {
						alert('매칭 처리 중 오류가 발생했습니다.');
					}
				});
			}
		}

		// 채팅 시작 함수
		function startChat(estimationNo) {
			if (confirm('해당 견적을 매칭하고 채팅을 시작하시겠습니까?')) {
				$.ajax({
					url: '/ajax/member/startChat',
					type: 'POST',
					data: {estimationNo: estimationNo},
					success: function (response) {
						if (response.success) {
							// 채팅 페이지로 이동
							window.location.href = `/chat/room/${response.roomId}`;
						} else {
							alert(response.message || '채팅 시작에 실패했습니다.');
						}
					},
					error: function () {
						alert('채팅 시작 중 오류가 발생했습니다.');
					}
				});
			}
		}
	});