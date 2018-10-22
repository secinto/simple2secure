import {Generic} from './generic';

export class DBConfig extends Generic {
  location: string;
  dbURI: string;
  write_user: string;
  write_password: string;
  read_user: string;
  read_password: string;
  time_slot_size: number;
}
