Ping'nPong ������ ������� �� ����� 80, �������� � ����� 200 ������ �� url /handler. � ��������� ������� ��� ������ 400

�������� ��������� ��������:
  hsqldb                         - ����������� hsqldb ���� ������(hsqldb.org)
  hsqldb\bin\runManagerSwing.bat - gui ������ ��� ������ � ����� ������
  src                            - �������� ���� ���������
  target\PingPongServer.jar      - ����������� ��������� Ping'n'Pong server
  target\startServer.bat         - ������ ��� ������� Ping'n'Pong � hsqldb ��������
  target\clientPage.html         - �������� �������� �������

������� ��� ������ ������� maven
  maven clean package

�������� ������, ����������� 1 ������������ ������� �PING <userId>�, �� ������� ������ ������ �������� �PONG N�, ��� N ���������� ���, ������� ���� ������ ���������� PING.
������ ������ �������� �� POST ������� �� ������ http://localhost/handler, ��� ������� � ��������� ������������ � JSON ������ ���� �������. � ����������� �������� ��, ��� ����� ������ � ������� ����� ����� (�� ������ PING).
��������� ������ ������� �������������� ������ ��������������, ��������, ��� ������ �������� ��� ������� ���������.