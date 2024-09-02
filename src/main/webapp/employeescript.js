const apiUrl = 'http://localhost:8080/LeaveManagementApp/employee/leave'; // Base API URL for the application
console.log(apiUrl);
let gh=document.getElementById("teamLeaveSummaryContainer");
console.log(gh);
let managerCheckToApproveOrReject=0;
let applyLeaveForm = document.getElementById('applyLeaveForm');
let appliedLeavesSection = document.getElementById('appliedLeaves');
let myTeamLeavesSection = document.getElementById('myTeamLeaves');
let leaves = [];
// Example array of holidays
// Function to populate leave summary cards
function updateLeaveCard(leave) {
    let cardId;
    console.log("leavessummaryteam",leave)
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
        console.log(leaveSummaryData);

        // Update only the relevant leave card
        leaveSummaryData.forEach(leave => {
         if (!leave.leaveType || !['sick leave', 'casual leave', 'paternity leave', 'maternity leave'].includes(leave.leaveType.toLowerCase())) {
                        console.warn('Skipping leave update due to unknown or missing leave type:', leave.leaveType);
                        return; // Skip this leave and move on to the next one
                    }
            updateLeaveCard(leave);
        });
    } catch (error) {
        console.error('Error fetching leave summary:', error);
    }
}
function updateTeamLeaveCard(leave, containerId) {
    let cardId;

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

    const container = document.getElementById(containerId);

        // Create a card element for each employee if it doesn't already exist
        let employeeCard = document.getElementById(`employee-${leave.employeeId}`);

        if (!employeeCard) {
            // Create a new card for the employee
            employeeCard = document.createElement('div');
            employeeCard.id = `employee-${leave.employeeId}`;
            employeeCard.className = 'card text-center mb-3';

            // Add employee name at the top of the card
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

        // Add leave type details to the existing employee card
        const cardBody = employeeCard.querySelector('.card-body');

        // Create a card element for the leave type
        const leaveCard = document.createElement('div');
            leaveCard.className = 'mb-2 border p-1'; // Optional styling for leave type card
            leaveCard.style.maxWidth = '300px'; // Limit the width of the leave type card to avoid excessive width
            leaveCard.style.width = '100%'; // Ensure the card takes up full available width
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
        console.log("leavesummarydata", leaveSummaryData);

        // Clear existing cards before updating
        const container = document.getElementById('teamLeaveSummaryContainer');
        container.innerHTML = '';

        // Update only the relevant leave cards
        leaveSummaryData.forEach(leave => {
            if (!leave.leaveType || !['sick leave', 'casual leave', 'paternity leave', 'maternity leave'].includes(leave.leaveType.toLowerCase())) {
                console.warn('Skipping leave update due to unknown or missing leave type:', leave.leaveType);
                return; // Skip this leave and move on to the next one
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
                    // Populate manager details
                    document.getElementById("managerName").innerText = employeeManager.managerName;
                    document.getElementById("managerEmail").innerText = employeeManager.managerEmail;
                    document.getElementById("managerPhone").innerText = employeeManager.managerPhoneNumber;
                    document.getElementById("managerGender").innerText = employeeManager.managerGender;
                    document.getElementById("managerDob").innerText = employeeManager.managerDateOfBirth;
                    document.getElementById("managerDetails").style.display = 'block'; // Show manager details section
                }
            document.getElementById("managerName").innerText = employeeManager.managerName;
            document.getElementById("managerEmail").innerText = employeeManager.managerEmail;
            document.getElementById("managerPhone").innerText = employeeManager.managerPhoneNumber;
            document.getElementById("managerGender").innerText = employeeManager.managerGender;
            document.getElementById("managerDob").innerText = employeeManager.managerDateOfBirth;
        }
document.getElementById("profileDetails").addEventListener("click", async function () {
    try {
       const response = await fetch(`${apiUrl}/getEmployeeAndManagerDetails`);
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
                <td>${leave.status}</td>
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
                <td>${leave.status}</td>
                <td>${leave.currentDate}</td>
                <td>${leave.totalEmployeeLeaves}</td>

                <td id="action-${leave.leaveId}">
                    ${actionCellContent}
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
console.log(leave.status);
    if (leave.status === 'PENDING') {
        return `
            <button id="accept-btn-${leave.leaveId}" onclick="approveLeave(${leave.leaveId})" class="btn btn-success">Accept</button>
            <button id="reject-btn-${leave.leaveId}" onclick="rejectLeave(${leave.leaveId})" class="btn btn-danger">Reject</button>
        `;
    } else if(leave.totalEmployeeLeaves>leave.typeLimit){
                    managerCheckToApproveOrReject=1;//total taken leaves greater than allocated limit leaves then manager will reject it
                    openModal("Leave Limit Exceeded", "You have already exceeded your leave limit for this type.");
                    return `<span class="text-danger">&#10008;</span>`;

                  }
    else if (leave.status === 'APPROVED') {
        return `<span class="text-success">&#10004;</span>`; // ✔ icon
    } else if (leave.status === 'REJECTED') {
        return `<span class="text-danger">&#10008;</span>`; // ✖ icon
    }

    return '';
}
// Approve Leave
async function approveLeave(leaveId) {
if(managerCheckToApproveOrReject==1){//if managerCheckToApproveOrReject is 1 then manager will reject the leave request
rejectLeave(leaveId);
}
else{
    try {
        const response = await fetch(`${apiUrl}/acceptLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        await response.json(); // Await the completion of the response
        fetchMyTeamLeaves("ALL"); // Refresh the team leaves section
    } catch (error) {
        console.error('Error approving leave:', error);
    }
}
}
// Reject Leave
async function rejectLeave(leaveId) {
    try {
        const response = await fetch(`${apiUrl}/rejectLeaveRequest?leaveId=${leaveId}`, {
            method: 'PUT'
        });
        if (!response.ok) throw new Error('Network response was not ok');
        await response.json(); // Await the completion of the response
        fetchMyTeamLeaves("ALL"); // Refresh the team leaves section
    } catch (error) {
        console.error('Error rejecting leave:', error);
    }
}
// Function to Show Section
function showSection(sectionId) {
    document.querySelectorAll('.form-section').forEach(section => {
        section.classList.remove('active');
    });
    document.getElementById(sectionId).classList.add('active');

    if (sectionId === 'appliedLeaves') {
        fetchAppliedLeaves("ALL"); // Fetch applied leaves when section is shown
    } else if (sectionId === 'myTeamLeaves') {
        fetchMyTeamLeaves("ALL"); // Fetch team leaves when section is shown
    } else if (sectionId === 'leaveSummaryCards') {
                    fetchLeaveSummary();
    } else if(sectionId === 'teamLeaveSummary'){
           fetchTeamLeaveSummary();
     } else if (sectionId === 'myTeamLeaves' || sectionId === 'teamLeaveSummary') {
                  document.getElementById('childNavbar').style.display = 'block';
              }
}
document.getElementById('statusFilter').addEventListener('change', (event) => {
    const selectedStatus = event.target.value;
    console.log("Selected status: " + selectedStatus);
    fetchAppliedLeaves(selectedStatus); // Fetch leaves based on selected status
});

document.getElementById('teamStatusFilter').addEventListener('change', (event) => {
    const selectedStatus = event.target.value;
    fetchMyTeamLeaves(selectedStatus); // Fetch leaves based on selected status
});
// Initialize by showing the Apply Leave section
document.addEventListener('DOMContentLoaded', () => {
    fetchEmployeeName();
    var profileContainer = document.getElementById('profileContainer');
    var dropdownMenu = document.getElementById('profileDropdown');
    var profileIcon = document.getElementById('profileIcon');
    // Add click event listener to the profile container
    profileContainer.addEventListener('click', function(event) {
        event.stopPropagation(); // Prevent the event from bubbling up
        // Toggle the visibility of the dropdown menu
        dropdownMenu.classList.toggle('show');
        // Toggle the profile icon between 'fa-user' and 'fa-user-circle'
        if (dropdownMenu.classList.contains('show')) {
            profileIcon.classList.remove('fa-user');
            profileIcon.classList.add('fa-user-circle');
        } else {
            profileIcon.classList.remove('fa-user-circle');
            profileIcon.classList.add('fa-user');
        }
    });
    // Close the dropdown if the user clicks outside of it
    document.addEventListener('click', function(event) {
        if (!profileContainer.contains(event.target)) {
            dropdownMenu.classList.remove('show');
            profileIcon.classList.remove('fa-user-circle');
            profileIcon.classList.add('fa-user');
        }
    });
});
// Function to fetch and display employee's name
async function fetchEmployeeName() {
    try {
        const response = await fetch(`${apiUrl}/getEmployeeName`);
        if (!response.ok) throw new Error('Network response was not ok');
        const employee = await response.json();
        console.log(employee);
        populateEmployeeDetails(employee);
         const employeeNameElement = document.getElementById('employeeName');
         employeeNameElement.textContent = employee.empName;// Update with the employee's name
         // If the employee is a manager (managerId is 0), hide the "Apply Leave" section
                 if (employee.managerId === 0) {
                     document.getElementById('applyLeaveForm').style.display = 'none'; // Hide apply leave form
                     document.getElementById('appliedLeaves').style.display = 'none';
                     document.getElementById('leaveSummaryCards').style.display = 'none';// Hide applied leaves section
                     // Optionally redirect to "My Team Leaves" section or show it by default
                     showSection('myTeamLeaves');
                 } else {
                     // If the employee is not a manager, show the "Apply Leave" section
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
    closeButton.onclick = function() {
        document.getElementById('customModal').style.display = 'none';
    };
    window.onclick = function(event) {
        const modal = document.getElementById('customModal');
        if (event.target == modal) {
            modal.style.display = 'none';
        }
    };
function isWeekend(date) {
    const dayOfWeek = date.getDay();
    return dayOfWeek === 6 || dayOfWeek === 0; // 6 = Saturday, 0 = Sunday
}
function calculateDateDifference(startDate, endDate) {
    const oneDay = 24 * 60 * 60 * 1000; // milliseconds in one day
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
             // Construct the URL with the URL-encoded leaveType for checking limits
             const checkLimitsUrl = `${apiUrl}/summary/getLeaveLimitsForLeaveType?leaveType=${leaveType}`;
             console.log("Check Limits URL:", checkLimitsUrl);
             try {
                 // Fetch leave limits and existing leaves
                 const limitsResponse = await fetch(checkLimitsUrl);
                 if (!limitsResponse.ok) throw new Error('Network response was not ok');
                 const leaveLimits = await limitsResponse.json();
                 console.log("Leave Limits:", leaveLimits);
                 const fromDate = new Date(leaveData.fromDate);
                 const toDate = new Date(leaveData.toDate);
                 const currentDateObj = new Date(formattedCurrentDate);
                 // Check if the dates are on weekends
                 if (isWeekend(fromDate) || isWeekend(toDate)) {
                     openModal("Invalid Leave Request", "You cannot apply for leave on a weekend (Saturday or Sunday).");
                     return;
                 }
                 // Check if the dates are in the past
                 if (fromDate < currentDateObj || toDate < currentDateObj) {
                     openModal("Invalid Leave Request", "The leave application time is in the past.");
                     return;
                 }
                 // Calculate leave duration
                 const leaveDuration = calculateDateDifference(fromDate, toDate);
                 console.log("Leave Duration (days):", leaveDuration);
                 // Check if the leave duration exceeds the available leave
                 const pendingLeaves =leaveLimits.typeLimit - leaveLimits.totalEmployeeLeaves;
                 if (leaveDuration > pendingLeaves) {
                     openModal("Exceeding Leave Limit", `You are exceeding your leave limit. You only have ${pendingLeaves} pending leave days left.`);
                     return;
                 }
                 // Check if the total leaves exceed the limit
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
