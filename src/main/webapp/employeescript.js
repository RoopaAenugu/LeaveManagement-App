const apiUrl = 'http://localhost:8080/LeaveManagementApp/employee/leave'; // Base API URL for the application
console.log(apiUrl);
let gh = document.getElementById("teamLeaveSummaryContainer");
console.log(gh);
let managerCheckToApproveOrReject = 0;
let applyLeaveForm = document.getElementById('applyLeaveForm');
let appliedLeavesSection = document.getElementById('appliedLeaves');
let myTeamLeavesSection = document.getElementById('myTeamLeaves');
let leaves = [];
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'long', day: 'numeric' };
    return new Date(dateString).toLocaleDateString(undefined, options);
}
async function fetchPersonalHolidays() {
    try {
        const response = await fetch(`${apiUrl}/summary/getPersonalHolidays`); // Adjust the API endpoint as needed
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching personal holidays:', error);
        return [];
    }
}
async function fetchHolidays() {
    try {
        const response = await fetch('http://localhost:8080/LeaveManagementApp/holidays');
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return await response.json();
    } catch (error) {
        console.error('Error fetching holidays:', error);
        return [];
    }
}
async function displayUpcomingHolidays() {
    const tableBody = document.querySelector('#upcomingHolidaysTableBody');
     tableBody.innerHTML = ''; // Clear existing content
      const reasonData = document.getElementById("upComingHolidayReason").style.display="none";
    const holidays = await fetchHolidays();
    const today = new Date();
    const endOfYear = new Date(today.getFullYear(), 11, 31);
    holidays
        .filter(holiday => new Date(holiday.holidayStartDate) >= today && new Date(holiday.holidayStartDate) <= endOfYear)
        .forEach(holiday => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${holiday.holidayName}</td>
                <td>${formatDate(holiday.holidayStartDate)}</td>
                <td>${formatDate(holiday.holidayEndDate)}</td>
            `;
            tableBody.appendChild(row);
        });
}
async function displayPastHolidays() {
    const tableBody = document.querySelector('#pastHolidaysTableBody');
    tableBody.innerHTML = ''; // Clear existing content
     const reasonData = document.getElementById("pastHolidayReason").style.display="none";
    const holidays = await fetchHolidays();
    const today = new Date();
    const startOfYear = new Date(today.getFullYear(), 0, 1); // January 1st of the current year
    const endOfYear = new Date(today.getFullYear(), 11, 31); // December 31st of the current year
    holidays
        .filter(holiday => {
            const holidayStartDate = new Date(holiday.holidayStartDate);
            return holidayStartDate < today && holidayStartDate >= startOfYear && holidayStartDate <= endOfYear;
        })
        .forEach(holiday => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${holiday.holidayName}</td>
                <td>${formatDate(holiday.holidayStartDate)}</td>
                <td>${formatDate(holiday.holidayEndDate)}</td>
            `;
            tableBody.appendChild(row);
        });
}
async function displayPersonalHolidays() {
    const tableBody = document.querySelector('#upcomingHolidaysTableBody');
    tableBody.innerHTML = ''; // Clear existing content
     const reasonData = document.getElementById("upComingHolidayReason").style.display="block";
    const holidays = await fetchPersonalHolidays();
    const today = new Date();
    const endOfYear = new Date(today.getFullYear(), 11, 31);
    holidays
        .filter(holiday => new Date(holiday.holidayStartDate) >= today && new Date(holiday.holidayStartDate) <= endOfYear)
        .forEach(holiday => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${holiday.holidayName}</td>
                <td>${formatDate(holiday.holidayStartDate)}</td>
                <td>${formatDate(holiday.holidayEndDate)}</td>
                <td>${holiday.reason}</td>
            `;
            tableBody.appendChild(row);
        });
}

async function displayPersonalPastHolidays() {
    const tableBody = document.querySelector('#pastHolidaysTableBody');
    tableBody.innerHTML = ''; // Clear existing content
    const reasonData = document.getElementById("pastHolidayReason").style.display="block";
    const holidays = await fetchPersonalHolidays();
    const today = new Date();
    const startOfYear = new Date(today.getFullYear(), 0, 1); // January 1st of the current year
    const endOfYear = new Date(today.getFullYear(), 11, 31); // December 31st of the current year

    holidays
        .filter(holiday => new Date(holiday.holidayStartDate) < today && new Date(holiday.holidayStartDate) >= startOfYear && new Date(holiday.holidayEndDate) <= endOfYear)
        .forEach(holiday => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${holiday.holidayName}</td>
                <td>${formatDate(holiday.holidayStartDate)}</td>
                <td>${formatDate(holiday.holidayEndDate)}</td>
                <td>${holiday.reason}</td>
            `;
            tableBody.appendChild(row);
        });
}
// Function to populate leave summary cards
function updateLeaveCard(leave) {
    let cardId;
    console.log("leavesSummaryTeam", leave)
    console.log(leave.leaveType);
    // Determine the card to update based on the leave type
    if (leave.leaveType.toLowerCase() === 'sick leave') {
        cardId = 'sickLeaveCard';
    } else if (leave.leaveType.toLowerCase() === 'casual leave') {
        cardId = 'casualLeaveCard';
    } else if (leave.leaveType.toLowerCase() === 'paternity leave') {
        cardId = 'paternityLeaveCard';
    } else if (leave.leaveType.toLowerCase() === 'maternity leave') {
        cardId = 'maternityLeaveCard';
    } else {
        console.error('Unknown leave type:', leave.leaveType);
        return;
    }
    // Get the card element
    const card = document.getElementById(cardId);
    // Update the card content with dynamic data
    card.innerHTML = `
        <div class="card text-center">
            <div class="card-header font-weight-bold ">
                ${leave.leaveType}
            </div>
            <div class="card-body">
                <p class="card-text"><strong>Total Leaves:</strong> ${leave.totalAllocatedLeaves}</p>
                <p class="card-text"><strong>Leaves Taken:</strong> ${leave.totalLeavesTaken}</p>
                <p class="card-text"><strong>Available Leaves:</strong> ${leave.pendingLeaves}</p>
            </div>
        </div>
    `;
}
// Fetch dynamic leave summary data
async function fetchLeaveSummary() {
    try {
        const response = await fetch(`${apiUrl}/summary/getEmployeeLeaveSummary`);
        if (!response.ok) throw new Error('Network response was not ok');
        const leaveSummaryData = await response.json();
        console.log("leaveSummaryData",leaveSummaryData);
        leaveSummaryData.forEach(leave => {
            if (!leave.leaveType || !['sick leave', 'casual leave', 'paternity leave', 'maternity leave'].includes(leave.leaveType.toLowerCase())) {
                console.warn('Skipping leave update due to unknown or missing leave type:', leave.leaveType);
                return; // Skip this leave and move on to the next one
            }
            updateLeaveCard(leave);
        });
        displayUpcomingHolidays();
        displayPastHolidays()
    } catch (error) {
        console.error('Error fetching leave summary:', error);
    }
}
function updateTeamLeaveCard(leave, containerId) {
    let cardId;
    if (leave.leaveType.toLowerCase() === 'sick leave') {
        cardId = 'sickLeaveCard';
    } else if (leave.leaveType.toLowerCase() === 'casual leave') {
        cardId = 'casualLeaveCard';
    } else if (leave.leaveType.toLowerCase() === 'paternity leave') {
        cardId = 'paternityLeaveCard';
    } else if (leave.leaveType.toLowerCase() === 'maternity leave') {
        cardId = 'maternityLeaveCard';
    } else {
        console.error('Unknown leave type:', leave.leaveType);
        return;
    }
    const container = document.getElementById(containerId);
    let employeeCard = document.getElementById(`employee-${leave.employeeId}`);
    if (!employeeCard) {
        employeeCard = document.createElement('div');
        employeeCard.id = `employee-${leave.employeeId}`;
        employeeCard.className = 'card text-center mb-3';
        employeeCard.innerHTML = `
                <div class="card-header font-weight-bold">
                    ${leave.empName}
                </div>
                <div class="card-body d-flex flex-row  justify-content-center"> <!-- Added flexbox classes -->
                                <!-- Leave type details will be added here -->
                            </div
            `;

        // Append the new employee card to the container
        container.appendChild(employeeCard);
    }
    const cardBody = employeeCard.querySelector('.card-body');
    const leaveCard = document.createElement('div');
    leaveCard.className = 'mb-2 border p-1';
    leaveCard.style.maxWidth = '300px';
    leaveCard.style.width = '100%';
    leaveCard.innerHTML = `
                <div class="card-header font-weight-bold text-center"> <!-- Centered the text -->
                    ${leave.leaveType}
                </div>
                <p class="card-text"><strong>Total Leaves:</strong> ${leave.totalAllocatedLeaves}</p>
                <p class="card-text"><strong>Leaves Taken:</strong> ${leave.totalLeavesTaken}</p>
                <p class="card-text"><strong>Available Leaves:</strong> ${leave.pendingLeaves}</p>
            `;
    // Append the leave type card to the employee's card body
    cardBody.appendChild(leaveCard);

}
async function fetchTeamLeaveSummary() {
    try {
        const response = await fetch(`${apiUrl}/summary/getTeamLeaveSummary`);
        if (!response.ok) throw new Error('Network response was not ok');
        const leaveSummaryData = await response.json();
        console.log("leaveSummaryData", leaveSummaryData);
        const container = document.getElementById('teamLeaveSummaryContainer');
        container.innerHTML = '';

        // Update only the relevant leave cards
        leaveSummaryData.forEach(leave => {
            if (!leave.leaveType || !['sick leave', 'casual leave', 'paternity leave', 'maternity leave'].includes(leave.leaveType.toLowerCase())) {
                console.warn('Skipping leave update due to unknown or missing leave type:', leave.leaveType);
                return;
            }
            updateTeamLeaveCard(leave, 'teamLeaveSummaryContainer');
        });
    } catch (error) {
        console.error('Error fetching leave summary:', error);
    }
}

function populateEmployeeDetails(employeeManager) {
    document.getElementById("empName").innerText = employeeManager.empName;
    document.getElementById("empEmail").innerText = employeeManager.email;
    document.getElementById("empDob").innerText = employeeManager.DateOfBirth;
    document.getElementById("empPhone").innerText = employeeManager.phoneNumber;
    document.getElementById("empGender").innerText = employeeManager.gender;
    const gender = employeeManager.gender.toLowerCase();
    if (gender === "male") {
        document.getElementById("maternityLeaveOption").style.display = "none";
        document.getElementById("maternityLeaveCard").style.display = "none"
    } else if (gender === "female") {
        document.getElementById("paternityLeaveOption").style.display = "none";
        document.getElementById("paternityLeaveCard").style.display = "none"

    }
    if (employeeManager.managerId === 0) {
        document.getElementById("managerDetails").style.display = 'none'; // Hide manager details section
    } else {
        document.getElementById("managerName").innerText = employeeManager.managerName;
        document.getElementById("managerEmail").innerText = employeeManager.managerEmail;
        document.getElementById("managerPhone").innerText = employeeManager.managerPhoneNumber;
        document.getElementById("managerDetails").style.display = 'block'; // Show manager details section
    }
    document.getElementById("managerName").innerText = employeeManager.managerName;
    document.getElementById("managerEmail").innerText = employeeManager.managerEmail;
    document.getElementById("managerPhone").innerText = employeeManager.managerPhoneNumber;

}
document.getElementById("profileDetails").addEventListener("click", async function () {
    try {
        const response = await fetch(`${apiUrl}/employeeDetails/getEmployeeAndManagerDetails`);
        if (!response.ok) throw new Error('Network response was not ok');
        const employeeManager = await response.json();
        console.log(employeeManager);
        populateEmployeeDetails(employeeManager);
        $('#employeeDetailsModal').modal('show');
    } catch (error) {
        console.error('Error fetching employee and manager details:', error);
    }
});
// Fetch and Render Applied Leaves
async function fetchAppliedLeaves(status) {
    try {
        console.log(status);
        const response = await fetch(`${apiUrl}/getAppliedLeaves?status=${status}`);
        if (!response.ok) throw new Error('Network response was not ok');
        leaves = await response.json();
        console.log(leaves);
        const tableBody = document.querySelector('#appliedLeaves table tbody');
        tableBody.innerHTML = ''; // Clear existing rows
        leaves.forEach(leave => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${leave.leaveType}</td>
                <td>${leave.fromDate}</td>
                <td>${leave.toDate}</td>
                <td>${leave.reason}</td>
                <td>${leave.typeLimit}</td>
                <td>
                   <span style="font-weight: bold; color: ${leave.status === 'PENDING' ? 'orange' : leave.status === 'APPROVED' ? 'green' : leave.status === 'REJECTED' ? 'red' : 'black'}; padding: 2px 4px;">
                        ${leave.status}
                   </span>
                </td>
                <td>${leave.currentDate}</td>

            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching applied leaves:', error);
    }
}
// Fetch and Render Team Leaves
async function fetchMyTeamLeaves(status) {
    try {
        const response = await fetch(`${apiUrl}/getMyTeamRequests?status=${status}`);
        if (!response.ok) throw new Error('Network response was not ok');
        const teamLeaves = await response.json();
        console.log("teamLeaves: ", teamLeaves);
        const tableBody = document.querySelector('#myTeamLeaves table tbody');
        tableBody.innerHTML = ''; // Clear existing rows
        teamLeaves.forEach(leave => {
            const actionCellContent = getActionCellContent(leave);

            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${leave.empName}</td>
                <td>${leave.leaveType}</td>
                <td>${leave.fromDate}</td>
                <td>${leave.toDate}</td>
                <td>${leave.reason}</td>
                 <td>
                     <span style="font-weight: bold; color: ${leave.status === 'PENDING' ? 'orange' : leave.status === 'APPROVED' ? 'green' : leave.status === 'REJECTED' ? 'red' : 'black'}; padding: 2px 4px;">
                         ${leave.status}
                     </span>
                 </td>
                <td>${leave.currentDate}</td>
                <td>${leave.totalEmployeeLeavesTaken}</td>

                <td id="action-${leave.leaveId}">
                    ${actionCellContent}
                </td>
               <td>
                    <button id="view-btn-${leave.leaveId}"
                          data-leave-id="${leave.leaveId}"
                           data-employee-id="${leave.employeeId}"
                            onclick="viewEmployeeDetails(this)"
                            class="btn btn-info">View</button>
                             </td>

            `;
            tableBody.appendChild(row);
        });
    } catch (error) {
        console.error('Error fetching team leaves:', error);
    }
}
// Helper function to determine the action cell content
function getActionCellContent(leave) {
    console.log("leaveAcceptAndReject",leave);
    const fromDate = new Date(leave.fromDate);
     const toDate = new Date(leave.toDate);
    let totalLeavesApply = countWeekdays(fromDate,toDate)
    console.log("totalLeavesApply",totalLeavesApply);
    if (leave.status === 'PENDING') {
       return `
                  <button id="accept-btn-${leave.leaveId}"
                          data-leave-id="${leave.leaveId}"
                          data-total-leaves-pending="${leave.pendingLeaves}"
                          data-total-leaves-apply="${totalLeavesApply}"
                          onclick="approveLeave(this)"
                          class="btn btn-success">Accept</button>
                  <button id="reject-btn-${leave.leaveId}"
                          data-leave-id="${leave.leaveId}"
                          onclick="rejectLeave(this)"
                          class="btn btn-danger">Reject</button>
              `;
    } else if (leave.status === 'APPROVED') {
        return `<span class="text-success">&#10004;</span>`; // ✔ icon
    } else if (leave.status === 'REJECTED') {
        return `<span class="text-danger">&#10008;</span>`; // ✖ icon
    }
    return '';
}
// Approve Leave
async function approveLeave(buttonElement) {
    const leaveId = buttonElement.getAttribute('data-leave-id');
    const totalEmployeeLeavesPending = parseInt(buttonElement.getAttribute('data-total-leaves-pending'), 10);
    const totalLeavesApply = parseInt(buttonElement.getAttribute('data-total-leaves-apply'), 10);
    console.log("leaveDetails", { leaveId, totalEmployeeLeavesPending, totalLeavesApply });

    if (totalLeavesApply > totalEmployeeLeavesPending) {
        openModal("Leave Limit Exceeded", "Employee have already exceeded  leave limit for this type.");
        rejectLeave(buttonElement);
        return;
    }

    try {
        const response = await fetch(`${apiUrl}/acceptLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        await response.json();
        fetchMyTeamLeaves("ALL");
        openModal("Success", "Leave approved successfully!");
    } catch (error) {
        console.error('Error approving leave:', error);
        openModal("Error", "Failed to approve leave. Please try again later.");
    }
}
// Reject Leave
async function rejectLeave(buttonElement) {
 const leaveId = buttonElement.getAttribute('data-leave-id');
    try {
        const response = await fetch(`${apiUrl}/rejectLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        await response.json();
        fetchMyTeamLeaves("ALL");
    } catch (error) {
        console.error('Error rejecting leave:', error);
    }
}
//view EmployeeDetails
async function viewEmployeeDetails(button) {
    const employeeId = button.getAttribute('data-employee-id');

        try {
            const response = await fetch(`${apiUrl}/employeeDetails/getEmployeeDetailsAndLeaveSummary?employeeId=${employeeId}`);
            if (!response.ok) throw new Error('Network response was not ok');
            const employeeDetails = await response.json();
            populateEmployeeDetailsAndSummaryLeaves(employeeDetails);

        } catch (error) {
            console.error('Error fetching employee details:', error);
        }
    }

    function populateEmployeeDetailsAndSummaryLeaves(employeeDetails) {

     document.getElementById("employee-details-dialog").style.display = "block";
    console.log("employeeDetails", employeeDetails);
        document.getElementById("Name").innerText = employeeDetails.empName;
        document.getElementById("employeeEmail").innerText = employeeDetails.email;
        document.getElementById("employeePhone").innerText = employeeDetails.phoneNumber;

 const tableBody = document.querySelector('#employee-details-dialog table tbody');
        tableBody.innerHTML = ''; // Clear existing rows
       employeeDetails.employeeLeaveSummaries.forEach(summary => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${summary.leaveType}</td>
                 <td>${summary.totalAllocatedLeaves}</td>
                <td>${summary.totalLeavesTaken}</td>
                <td>${summary.pendingLeaves}</td>
            `;
            tableBody.appendChild(row);
                      });

    }
    function closeDialog() {
        document.getElementById("employee-details-dialog").style.display = "none";
    }

// Function to Show Section
function showSection(sectionId) {
    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');
    if (sectionId === 'appliedLeaves') {
        fetchAppliedLeaves("ALL");
    } else if (sectionId === 'myTeamLeaves') {
        fetchMyTeamLeaves("ALL");
    } else if (sectionId === 'leaveSummaryCards') {
        fetchLeaveSummary();
    } else if (sectionId === 'teamLeaveSummary') {
        fetchTeamLeaveSummary();
    } else if (sectionId === 'myTeamLeaves' || sectionId === 'teamLeaveSummary') {
        document.getElementById('childNavbar').style.display = 'block';
    }
}
document.getElementById('statusFilter').addEventListener('change', (event) => {
    const selectedStatus = event.target.value;
    console.log("Selected status: " + selectedStatus);
    fetchAppliedLeaves(selectedStatus);
});

document.getElementById('teamStatusFilter').addEventListener('change', (event) => {
    const selectedStatus = event.target.value;
    fetchMyTeamLeaves(selectedStatus);
});

document.getElementById('holidayFilter').addEventListener('change', (event) => {
    const selectedHoliday = event.target.value;
    console.log(selectedHoliday);
    if (selectedHoliday === "general") {
        displayUpcomingHolidays();
    } else if (selectedHoliday === "personal") {
        displayPersonalHolidays();
    }
});
document.getElementById('pastHolidayFilter').addEventListener('change', (event) => {
    const selectedHoliday = event.target.value;
    console.log(selectedHoliday);
    if (selectedHoliday === "general") {
        displayPastHolidays();
    } else if (selectedHoliday === "personal") {
        displayPersonalPastHolidays();
    }
});

// Initialize by showing the Apply Leave section
document.addEventListener('DOMContentLoaded', () => {
    fetchEmployeeName();
    var profileContainer = document.getElementById('profileContainer');
    var dropdownMenu = document.getElementById('profileDropdown');
    var profileIcon = document.getElementById('profileIcon');
    profileContainer.addEventListener('click', function (event) {
        event.stopPropagation();
        dropdownMenu.classList.toggle('show');
        if (dropdownMenu.classList.contains('show')) {
            profileIcon.classList.remove('fa-user');
            profileIcon.classList.add('fa-user-circle');
        } else {
            profileIcon.classList.remove('fa-user-circle');
            profileIcon.classList.add('fa-user');
        }
    });
    document.addEventListener('click', function (event) {
        if (!profileContainer.contains(event.target)) {
            dropdownMenu.classList.remove('show');
            profileIcon.classList.remove('fa-user-circle');
            profileIcon.classList.add('fa-user');
        }
    });
});
async function fetchEmployeeName() {
    try {
        const response = await fetch(`${apiUrl}/employeeDetails/getEmployeeName`);
        if (!response.ok) throw new Error('Network response was not ok');
        const employee = await response.json();
        console.log(employee);
        populateEmployeeDetails(employee);
        const employeeNameElement = document.getElementById('employeeName');
        employeeNameElement.textContent = employee.empName;
        if (employee.managerId === 0) {
            document.getElementById('applyLeave').style.display = 'none';
            document.getElementById('appliedLeaves').style.display = 'none';
            document.getElementById('leaveSummaryCards').style.display = 'none';
            showSection('myTeamLeaves');
        } else {
            showSection('leaveSummaryCards');
        }
    } catch (error) {
        console.error('Error fetching employee name:', error);
    }
}
// Function to open the modal with a specific message
function openModal(title, message) {
    const modal = document.getElementById('customModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modal.style.display = 'block';
}
const closeButton = document.getElementById('modalCloseButton');
closeButton.onclick = function () {
    document.getElementById('customModal').style.display = 'none';
};
window.onclick = function (event) {
    const modal = document.getElementById('customModal');
    if (event.target == modal) {
        modal.style.display = 'none';
    }
};
function countWeekdays(startDate, endDate) {
    let count = 0;
    let currentDate = new Date(startDate);
    while (currentDate <= endDate) {
        const dayOfWeek = currentDate.getDay();
        if (dayOfWeek !== 6 && dayOfWeek !== 0) {
            count++;
        }
        currentDate.setDate(currentDate.getDate() + 1);
    }
    return count;
}
function isWeekend(date) {
    const dayOfWeek = date.getDay();
    return dayOfWeek === 6 || dayOfWeek === 0;
}
function calculateDateDifference(startDate, endDate) {
    const oneDay = 24 * 60 * 60 * 1000;
    return Math.round(Math.abs((endDate - startDate) / oneDay)) + 1; // Adding 1 to include the start date
}
applyLeaveForm.addEventListener('submit', async (event) => {
    event.preventDefault();
    const formData = new FormData(applyLeaveForm);
    const currentDate = new Date();
    // Format the date to YYYY-MM-DD
    const formattedCurrentDate = currentDate.toISOString().split('T')[0];
    // Get leaveType and URL-encode it
    const leaveType = encodeURIComponent(formData.get('leaveType'));
    // Create the leaveData object with the current date
    const leaveData = {
        leaveType: formData.get('leaveType'), // This will be used in the body
        fromDate: formData.get('fromDate'),
        toDate: formData.get('toDate'),
        reason: formData.get('reason'),
        currentDate: formattedCurrentDate
    };
    console.log("leaveData:", leaveData);
    const checkLimitsUrl = `${apiUrl}/summary/getLeaveLimitsForLeaveType?leaveType=${leaveType}`;
    console.log("Check Limits URL:", checkLimitsUrl);
    try {
        const limitsResponse = await fetch(checkLimitsUrl);
        if (!limitsResponse.ok) throw new Error('Network response was not ok');
        const leaveLimits = await limitsResponse.json();
        console.log("Leave Limits:", leaveLimits);
        const fromDate = new Date(leaveData.fromDate);
        const toDate = new Date(leaveData.toDate);
        const currentDateObj = new Date(formattedCurrentDate);
        if (isWeekend(fromDate) || isWeekend(toDate)) {
            openModal("Invalid Leave Request", "You cannot apply for leave on a weekend (Saturday or Sunday).");
            return;
        }
        if (fromDate < currentDateObj || toDate < currentDateObj) {
            openModal("Invalid Leave Request", "The leave application time is in the past.");
            return;
        }
        const leaveDuration = countWeekdays(fromDate, toDate);
        const pendingLeaves = leaveLimits.typeLimit - leaveLimits.totalEmployeeLeavesTaken;
        if (leaveDuration > pendingLeaves) {
            openModal("Exceeding Leave Limit", `You are exceeding your leave limit. You only have ${pendingLeaves} pending leave days left.`);
            return;
        }
        if (leaveLimits.totalEmployeeLeaves >= leaveLimits.typeLimit) {
            openModal("Leave Limit Exceeded", "You have already exceeded your leave limit for this type.");
            return;
        }
        // If all validations pass, send the request to apply leave
        const applyResponse = await fetch(`${apiUrl}/applyEmployeeLeave?leaveType=${leaveType}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(leaveData)
        });
        if (applyResponse.ok) {
            const responseData = await applyResponse.json();
            console.log("Response data:", responseData);
            openModal("Success", "Leave applied successfully!");
            showSection('appliedLeaves');
        } else {
            console.error('Response status:', applyResponse.status);
            console.error('Response status text:', applyResponse.statusText);
            openModal("Failure", "Failed to apply leave.");
        }
    } catch (error) {
        console.error('Error applying leave:', error);
        openModal("Error", "Failed to apply leave. Please try again later.");
    }
});
