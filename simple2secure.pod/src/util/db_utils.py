import logging

from src.db.database import db

log = logging.getLogger('pod.util.db_utils')


def init_db(app):
    with app.app_context():
        db.create_all()
        db.session.commit()


def update(some_object):
    db.session.add(some_object)
    db.session.commit()
