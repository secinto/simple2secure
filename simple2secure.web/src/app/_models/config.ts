import {Generic} from './generic';
import {DBConfig} from './dbconfig';
import {QueryConfig} from './queryconfig';
import {API} from './api';

export class Config extends Generic {
  version: number;
  probeId: string;
  config_supplier: string;
  task_supplier: string;
  stylesheet: string;
  use_configured_iface: boolean;
  interface_number: number;
  show_interfaces: boolean;
  external_address: string;
  connection_timeout: number;
  processing_factory: string;
  communication_factory: string;
  wt_intervall: number;
  base_url: string;
  db_config: DBConfig;
  queries: QueryConfig;
  apis: API[];
  groupConfiguration: boolean;
}
