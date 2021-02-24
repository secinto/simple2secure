from src.db.database import Session


class SessionManager:
    """
    Class to create and destroy sessions for db access -> use in python with-statement.

        Returns:
            Valid session to access the database.
    """
    def __init__(self):
        # Creates a new valid session to the db
        self.session = Session()

    def __enter__(self):
        return self.session

    def __exit__(self, exc_type, exc_val, exc_tb):
        if exc_tb is None:
            # No exception, so commit
            self.session.commit()
            # Send persistent instances to the detached state
            self.session.expunge_all()
            # Close the session
            self.session.close()
        else:
            # Exception occurred, so rollback.
            self.session.rollback()
            # return False
