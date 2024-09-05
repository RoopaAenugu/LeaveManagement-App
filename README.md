# Leave Management App

## Overview

The Leave Management App is designed to streamline the process of managing employee leave requests. It offers features for applying, approving, and reviewing leave requests and includes functionalities for both employees and managers.

## Features

### 1. Leave Summary Cards

- **Description**: Displays a summary of leave balances for different leave types (e.g., Sick Leave, Casual Leave, Paternity Leave, Maternity Leave).
- **Functionality**: Dynamically updates based on the employee's leave data fetched from the server.

### 2. Holiday Management

- **Upcoming Holidays**:
   - **Description**: Displays a list of upcoming holidays for the current year.
   - **Functionality**: Filters out holidays that have already passed and only shows those that are yet to occur.

- **Past Holidays**:
   - **Description**: Shows past holidays within the current year.
   - **Functionality**: Allows users to review holidays that have already occurred.

- **Personal Holidays (Upcoming)**:
   - **Description**: Displays personal holidays for the current year, including reasons for each leave.
   - **Functionality**: Helps employees keep track of their own leave history.

- **Personal Holidays (Past)**:
   - **Description**: Similar to displaying upcoming personal holidays, but for past holidays.
   - **Functionality**: Includes the reason for each leave, providing a record of all personal leave taken.

### 3. Apply Leave

- **Description**: Allows employees to submit leave requests.
- **Functionality**: Users can specify leave type, dates, and reasons. The system checks leave limits and validity before submission.

### 4. View Applied Leaves

- **Description**: Shows a list of leave requests submitted by the employee.
- **Functionality**: Provides a table view with details such as leave type, dates, reason, and status.

### 5. Team Leave Summary

- **Description**: Displays a summary of leave requests for the employee's team.
- **Functionality**: Managers can see leave details for all team members, categorized by leave type.

### 6. Approve/Reject Leave Requests

- **Description**: Allows managers to review and take action on leave requests.
- **Functionality**: Provides buttons to accept or reject leave requests. The status is updated accordingly, and the table view is refreshed.

### 7. Employee Profile Details

- **Description**: Displays detailed information about the logged-in employee and their manager.
- **Functionality**: Shows personal details, manager information, and adjusts leave options based on the employee's gender.

### 8. Date Validation and Calculations

- **Description**: Ensures leave requests are valid based on the current date and calculates the number of weekdays between leave dates.
- **Functionality**: Validates leave dates to prevent applications on weekends or past dates, and calculates the duration of leave.

### 9. Modal Notifications

- **Description**: Provides feedback to users about the success or failure of their actions.
- **Functionality**: Shows modals with appropriate messages based on user actions or errors.

## Leave Application Form Validations

When applying for leave, several validations are performed to ensure the request is valid and within the allowed limits. Below are the details of each validation step:

### 1. Weekend Validation

- **Description**: Checks if the leave dates fall on a weekend (Saturday or Sunday).
- **Purpose**: Prevents users from applying for leave on weekends.
- **Implementation**:
   - Uses the `isWeekend` function to determine if the `fromDate` or `toDate` is a Saturday or Sunday.
   - **Error Message**: If either date is a weekend, a modal displays: `"You cannot apply for leave on a weekend (Saturday or Sunday)."`

### 2. Past Date Validation

- **Description**: Ensures that the leave application dates are not in the past.
- **Purpose**: Prevents users from applying for leave for dates that have already passed.
- **Implementation**:
   - Compares the `fromDate` and `toDate` with the current date.
   - **Error Message**: If either date is in the past, a modal displays: `"The leave application time is in the past."`

### 3. Leave Duration and Limit Validation

- **Description**: Checks whether the requested leave duration exceeds the available leave balance.
- **Purpose**: Ensures that the user does not exceed their leave balance.
- **Implementation**:
   - Calculates the number of weekdays between `fromDate` and `toDate` using the `countWeekdays` function.
   - Compares the calculated leave duration with the available leave balance (`pendingLeaves`), which is derived from `leaveLimits.typeLimit` minus `leaveLimits.totalEmployeeLeavesTaken`.
   - **Error Message**: If the leave duration exceeds the available balance, a modal displays: `"You are exceeding your leave limit. You only have X pending leave days left."`

### 4. Leave Type Limit Validation

- **Description**: Ensures that the user has not exceeded the maximum allowed leave limit for a specific leave type.
- **Purpose**: Prevents users from applying for more leave than allowed for a specific type (e.g., sick leave, casual leave).
- **Implementation**:
   - Checks if the total leaves for the specific type (`leaveLimits.totalEmployeeLeaves`) have already reached or exceeded the allowed limit (`leaveLimits.typeLimit`).
   - **Error Message**: If the leave type limit is exceeded, a modal displays: `"You have already exceeded your leave limit for this type."`

### 5. Successful Leave Application

- **Description**: If all validations pass, the leave request is submitted.
- **Purpose**: To apply for leave once all checks have been successfully passed.
- **Implementation**:
   - Sends a POST request to the server with the leave data.
   - **Success Message**: If the request is successful, a modal displays: `"Leave applied successfully!"` and the `appliedLeaves` section is displayed.

### 6. Error Handling

- **Description**: Catches and handles any errors that occur during the leave application process.
- **Purpose**: To provide feedback to the user in case of network errors or server issues.
- **Implementation**:
   - Catches exceptions thrown during the validation and submission process.
   - **Error Message**: Displays a modal with the message: `"Failed to apply leave. Please try again later."` if an error occurs.


## Manager Leave Request Approval

When a leave request is submitted, managers need to review and approve or reject the request. The following validations and checks are performed as part of the approval process:

### 1. Leave Request Validation

Before a leave request is approved or rejected, the system performs several checks to ensure that the request meets the necessary criteria:

- **Weekend Validation**: Ensure that the leave dates do not fall on weekends. Requests for leave on weekends are automatically flagged as invalid.
- **Past Date Validation**: Ensure that the leave application dates are not in the past. Requests with past dates are flagged as invalid.
- **Leave Duration and Limit Validation**: Check if the requested leave duration exceeds the employee's available leave balance. Requests that exceed the balance are flagged as invalid.
- **Leave Type Limit Validation**: Ensure that the leave request does not exceed the maximum allowed leave limit for the specific type of leave (e.g., sick leave, casual leave). Requests that exceed this limit are flagged as invalid.

### 2. Manager's Approval Process

Managers must review the following details before approving or rejecting a leave request:

- **Leave Details**: Confirm that the leave type, start and end dates, and reason for leave are correct and align with company policies.
- **Leave Balance**: Verify that the employee has sufficient leave balance for the requested leave type.
- **Leave Limits**: Ensure that the leave request does not exceed any predefined limits for the leave type or overall leave balance.
- **Conflict Checks**: Assess if there are any conflicts with other employees' leaves or critical work periods. For example, ensure that multiple employees in the same team are not on leave simultaneously during critical periods.

### 3. Actions for Managers

- **Approve Leave**:
   - If the request passes all validations, the manager can approve the leave.
   - The system will then update the leave status and notify the employee.
   - **Success Message**: The employee is notified with a message: `"Success", "Leave approved successfully!."`

- **Reject Leave**:
   - If the request does not meet the criteria or conflicts with company policies, the manager can reject the leave.
   - The system will then update the leave status and notify the employee with reasons for rejection.
   - **Failure Message**: The employee is notified with a message: `"Success", "Leave rejected successfully!."`

### 4. Error Handling

- **Network or Server Issues**: If an error occurs during the approval or rejection process, the manager will receive an error message indicating a failure in processing the request.
   - **Error Message**: `"An error occurred while processing the leave request. Please try again later or contact support."`

Login Page:
![LoginPage](https://github.com/user-attachments/assets/7a2c4173-90ea-4d16-afb1-be4d2ffd4e1d)
Employee Leave Summary Page:
<img width="949" alt="EmployeeLeaveSummary" src="https://github.com/user-attachments/assets/8e44a27a-4721-46ca-8a5e-dec37aa4080c">

Employee UpComing Holidays Page:

<img width="677" alt="upComingHolidays" src="https://github.com/user-attachments/assets/d067624f-e82f-403d-affd-83d9cf4bd071">

Employee Past Holidays Page :

<img width="659" alt="pastHolidays" src="https://github.com/user-attachments/assets/9ad1e5fd-2002-4835-9fe4-3ba1c47bca4e">

Employee upComing and Past Leaves Page:

<img width="653" alt="myUpcomingAndPastLeaves" src="https://github.com/user-attachments/assets/6cd24fe0-40f1-468f-9e34-bcbfb4ae8b26">


Employee Applied Leaves Page :
<img width="946" alt="Employee Applied Leaves" src="https://github.com/user-attachments/assets/f0a77239-8626-4311-90ae-3b119607d023">

Employee ApplyLeave Page:
<img width="914" alt="applyLeave" src="https://github.com/user-attachments/assets/63a119fd-9c6a-4dbb-9653-86eea0fb08a4">

Success Applying Message:

 <img width="721" alt="appliedSuccess" src="https://github.com/user-attachments/assets/9c0cdb4a-a3c4-400b-9386-023e25228059">

Employee Profile Page :

<img width="262" alt="profile Page" src="https://github.com/user-attachments/assets/1e7db223-9e4c-4464-ba78-4ae2b7af1123">


Team Leave Summary Page:

<img width="847" alt="TeamLeaveSummary" src="https://github.com/user-attachments/assets/e922abee-c262-40a9-8c63-1996436b69de">


Team Leave Requests Page:
<img width="919" alt="TeamLeaveRequests" src="https://github.com/user-attachments/assets/facd5b76-64d0-4e80-b807-95adb9b8ebb4">

Employee view Details Page

<img width="356" alt="EmployeeDetails" src="https://github.com/user-attachments/assets/44198818-1312-419a-a6bb-6915221ec3a0">

Success Approved Page:

<img width="385" alt="approveSuccessPage" src="https://github.com/user-attachments/assets/aaef1627-4b51-446f-9b6a-2659dbcf933d">

Success Rejected Page :

<img width="346" alt="successLeaveReject" src="https://github.com/user-attachments/assets/400b1030-43a2-4e30-b7ad-b944e28e2ce4">


Validations :

1. Validation For PreDate:
   <img width="515" alt="validationForPastTime" src="https://github.com/user-attachments/assets/13abac79-8b36-4f0a-af78-254d6e70cf8d">

2. Validation For TotalLeaves:
   <img width="450" alt="validationForTotalLeaves" src="https://github.com/user-attachments/assets/f821185a-2d77-45b5-9159-e9d739041bc5">

3.Validation For Weekend Date:

<img width="434" alt="validationForWeekends" src="https://github.com/user-attachments/assets/58e5cbcc-8b4c-484a-81ad-fe13811438ca">

4.Validation For Approving Leave Request:

<img width="434" alt="managerAcceptValidation" src="https://github.com/user-attachments/assets/534b7261-1c1d-492d-b616-83e62dddfe4a">
