from src.db.database import db


def update(some_object):
    db.session.add(some_object)
    db.session.commit()

