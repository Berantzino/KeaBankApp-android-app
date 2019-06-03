# KeaBankApp

## Application UI
![sign_in](https://user-images.githubusercontent.com/36447407/58831856-eee37a80-864d-11e9-811d-3d6734abf461.png)
![sign_up](https://user-images.githubusercontent.com/36447407/58831857-ef7c1100-864d-11e9-9458-e0cbcd7e64b9.png)
![accounts](https://user-images.githubusercontent.com/36447407/58831860-ef7c1100-864d-11e9-932d-b4426a0e4445.png)
![account_details](https://user-images.githubusercontent.com/36447407/58831862-ef7c1100-864d-11e9-9c50-6cc8592b864c.png)
![transaction_details](https://user-images.githubusercontent.com/36447407/58831864-ef7c1100-864d-11e9-89b8-8a9580edeca0.png)
![add_new_account](https://user-images.githubusercontent.com/36447407/58831867-f014a780-864d-11e9-9658-4e180e2a146f.png)
![user_details](https://user-images.githubusercontent.com/36447407/58831868-f014a780-864d-11e9-93a5-6e3a8446b2ea.png)
![pay_bill](https://user-images.githubusercontent.com/36447407/58831869-f014a780-864d-11e9-959b-16b56e2fbfa1.png)
![transfer](https://user-images.githubusercontent.com/36447407/58831870-f014a780-864d-11e9-9155-3a5a96140a56.png)
![transfer_my_accounts](https://user-images.githubusercontent.com/36447407/58831871-f014a780-864d-11e9-9629-28a4e10e17a9.png)
![transfer_to_others](https://user-images.githubusercontent.com/36447407/58831872-f0ad3e00-864d-11e9-8518-295a017b1267.png)

## Assignment Solution
The assignment was to develop an Android Application for the customers of KEA bank.  
They expected to be able to do almost everything they can in their browser online bank.  
  
The solution makes use of Google's Firebase Firestore, in order to store the data.

## To run the Application you must clone the repository or down it as zip and open it in Android Studio
`git clone https://github.com/lass5643/KeaBankApp-android.git`

### Login System
* **When the app is launched, you arrive at the sign in screen with the following three options**
  * Sign in with existing credentials
  * Sign up to the bank
    * The sign up requires your permission to use your location
    * This is because the bank assigns you to the affiliate closest to you
  * Recover/reset your password, in case you've forgotten it, but already have an account
    * You will be sent an email, containing a link to reset your password
* **After you've successfully signed in, you arrive at the account overview**
  * Click any of your accounts for further details
  * Click the + at the top to add a new account
  * Click the outlined image of a person at the top for user details
  * Click the square with an arrow to sign out
  * Click either of the three options at the bottom to go to their respective Activities
* **Account Details**
  * This activity shows you the accounts details for the account you clicked
  * It shows the account name, balance and a list of all transaction from/to that account
* **Transaction Details**
  * Displays all details about a the specific transaction you clicked on the account details activity
* **Add New Account**
  * Here you can add a new account
  * Simply enter an account name and select the account type from the dropdown menu
  * You will automatically receive a budget and default account
* **User Details**
  * Displays the user details provided at sign up
* **Pay Bill**
  * Pay a bill to any of the services in the dropdown menu (for test purposes hardcoded)
  * Select the amount and choose weither or not it should be paid automatically from here on out
  * Pay automatically is currently a boolean only with no added actions
* **Transfer**
  * Choose if you want to transfer between your own accounts or to someone else
* **Transfer between your own accounts**
  * Write a title for the transaction (default is "Transfer")
  * Set the amount (cannot be higher than your balance)
  * Select the to and from account from the two dropdown menus
    * In order to transfer from a pension account you must be atleast 77 years of age
    * Transfer to a pension account requires verifation with NEM ID
  * Write a message if you want to (optional)
* **Transfer to others accounts**
  * Same options as transfer between own accounts
  * The only difference is that the **To account** dropdown menu contains all accounts in the back that are not yours (for     simulation purposes)
