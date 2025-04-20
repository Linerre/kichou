#!/usr/bin/env python3
import os
import sqlite3
from google.oauth2.service_account import Credentials
from googleapiclient.discovery import build
from config import SPREADSHEET_ID, CREDS_PATH, DB_PATH, EXPENSE_YEAR2025


def create_database(db_path):
    """Create the SQLite database with the required schema."""
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Create Providers table with simpler syntax
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS Providers (
        abn TEXT PRIMARY KEY,
        full_brand_name TEXT NOT NULL,
        location TEXT,
        website TEXT
    )
    ''')

    # Create Expenses table with simpler syntax
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS Expenses (
        expense_id INTEGER PRIMARY KEY AUTOINCREMENT,
        date TEXT NOT NULL,
        week_number INTEGER NOT NULL,
        product_name TEXT NOT NULL,
        price REAL NOT NULL,
        quantity INTEGER NOT NULL,
        provider_abn TEXT NOT NULL,
        FOREIGN KEY (provider_abn) REFERENCES Providers(abn)
    )
    ''')

    # Create helpful indexes
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_expenses_date ON Expenses(date)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_expenses_week ON Expenses(week_number)')
    cursor.execute('CREATE INDEX IF NOT EXISTS idx_expenses_provider ON Expenses(provider_abn)')

    conn.commit()
    conn.close()

def get_sheets_data(spreadsheet_id, credential_path):
    """Connect to Google Sheets API and get data."""
    # Set up credentials
    scopes = ['https://www.googleapis.com/auth/spreadsheets.readonly']
    creds = Credentials.from_service_account_file(credential_path, scopes=scopes)

    # Build the service
    service = build('sheets', 'v4', credentials=creds)

    # Get data from expenses sheet
    expenses_result = service.spreadsheets().values().get(
        spreadsheetId=spreadsheet_id, range=f'{EXPENSE_YEAR2025}').execute()
    expenses_values = expenses_result.get('values', [])

    # Get data from providers sheet
    providers_result = service.spreadsheets().values().get(
        spreadsheetId=spreadsheet_id, range='providers!A:E').execute()
    providers_values = providers_result.get('values', [])

    return expenses_values, providers_values

def import_to_sqlite(expenses_data, providers_data, db_path):
    """Import the data to SQLite."""
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Process providers data (assuming headers are in first row)
    headers = providers_data[0]
    for provider in providers_data[1:]:
        # Ensure row matches headers length for safe zipping
        padded_provider = provider + [''] * (len(headers) - len(provider))
        provider_dict = dict(zip(headers, padded_provider))

        cursor.execute('''
        INSERT OR REPLACE INTO Providers (abn, full_brand_name, website, location)
        VALUES (?, ?, ?, ?)
        ''', (
            provider_dict.get('ABN', ''),
            provider_dict.get('Brand Full Name', ''),
            provider_dict.get('Website', ''),
            provider_dict.get('Location', '')
        ))

    # Process expenses data (assuming headers are in first row)
    headers = expenses_data[0]
    for expense in expenses_data[1:]:
        # Ensure row matches headers length for safe zipping
        padded_expense = expense + [''] * (len(headers) - len(expense))
        expense_dict = dict(zip(headers, padded_expense))

        cursor.execute('''
        INSERT INTO Expenses (date, week_number, product_name, price, quantity, provider_abn)
        VALUES (?, ?, ?, ?, ?, ?)
        ''', (
            expense_dict.get('Date', ''),
            int(expense_dict.get('Week', 0)),
            expense_dict.get('ProductName', ''),
            float(expense_dict.get('Price', 0)),
            int(expense_dict.get('Quantity', 0)),
            expense_dict.get('Provider', '')  # This should match an ABN in Providers table
        ))

    conn.commit()
    conn.close()

def main():

    # Create database directory if it doesn't exist
    os.makedirs(os.path.dirname(DB_PATH), exist_ok=True)

    # Create the database schema
    create_database(DB_PATH)

    # Get data from Google Sheets
    expenses_data, providers_data = get_sheets_data(SPREADSHEET_ID, CREDS_PATH)

    # Import data to SQLite
    import_to_sqlite(expenses_data, providers_data, DB_PATH)

    print(f"Data successfully imported to {DB_PATH}")

if __name__ == "__main__":
    main()
