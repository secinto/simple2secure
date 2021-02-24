import unittest
import logging

from scripts.connect import get_request

log = logging.getLogger('test.pod.scripts.connect')


class TestGETConnect(unittest.TestCase):

    def test_split(self):
        resp = get_request("https://orf.at/")
        log.info(resp.cookies)


if __name__ == '__main__':
    unittest.main()